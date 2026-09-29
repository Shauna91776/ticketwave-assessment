'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');

const { formatPrice, filterEvents, renderEventCards, submitBooking } = require('../app.js');

const html = fs.readFileSync(path.join(__dirname, '..', 'index.html'), 'utf8');
const css = fs.readFileSync(path.join(__dirname, '..', 'styles.css'), 'utf8');

function tagContaining(source, tagName, needle) {
  const tags = source.match(new RegExp(`<${tagName}\\b[^>]*>`, 'gi')) || [];
  return tags.find((tag) => tag.includes(needle));
}

function ruleFor(selector) {
  const escaped = selector.replace(/[.*+?^${}()|[\]\\:]/g, '\\$&');
  const matches = [...css.matchAll(new RegExp(`(?:^|})\\s*([^{}]*${escaped}[^{}]*)\\{([^}]*)\\}`, 'g'))];
  return matches.map((m) => m[2]).join('\n');
}

// ---------------------------------------------------------------- Q8.1 HTML

test('Q8.1 html declares its language', () => {
  assert.match(html, /<html[^>]*\blang="en"/i);
});

test('Q8.1 page uses header, nav and main landmarks instead of generic divs', () => {
  assert.match(html, /<header\b/i);
  assert.match(html, /<main\b/i);
  assert.match(html, /<nav\b[^>]*aria-label="Main"/i);
  assert.doesNotMatch(html, /<div class="top"/i);
  assert.doesNotMatch(html, /<div class="main"/i);
  assert.doesNotMatch(html, /<div class="menu"/i);
});

test('Q8.1 logo image has meaningful alternative text', () => {
  const img = tagContaining(html, 'img', 'class="logo"');
  assert.ok(img, 'expected the logo <img>');
  assert.match(img, /\balt="[^"]+"/);
});

test('Q8.1 search box is a labelled search field', () => {
  assert.match(html, /<label[^>]*\bfor="search"/i);
  const input = tagContaining(html, 'input', 'id="search"');
  assert.ok(input, 'expected an <input> with id="search"');
  assert.match(input, /\btype="search"/);
});

test('Q8.1 booking details are grouped and validated', () => {
  assert.match(html, /<fieldset\b/i);
  assert.match(html, /<legend\b/i);

  assert.match(html, /<label[^>]*\bfor="email"/i);
  const email = tagContaining(html, 'input', 'id="email"');
  assert.ok(email, 'expected an <input> with id="email"');
  assert.match(email, /\bname="email"/);
  assert.match(email, /\btype="email"/);
  assert.match(email, /\brequired\b/);

  assert.match(html, /<label[^>]*\bfor="quantity"/i);
  const qty = tagContaining(html, 'input', 'id="quantity"');
  assert.ok(qty, 'expected an <input> with id="quantity"');
  assert.match(qty, /\bname="quantity"/);
  assert.match(qty, /\btype="number"/);
  assert.match(qty, /\bmin="1"/);
  assert.match(qty, /\bmax="8"/);
  assert.match(qty, /\brequired\b/);

  assert.match(html, /<button[^>]*\btype="submit"/i);
});

test('Q8.1 events list and message region are wired for assistive technology', () => {
  const section = tagContaining(html, 'section', 'id="events"');
  assert.ok(section, 'expected <section id="events">');
  assert.match(section, /aria-labelledby="events-heading"/);
  const message = tagContaining(html, '(?:div|p|section)', 'id="message"');
  assert.ok(message, 'expected the #message element to remain');
  assert.match(message, /role="status"/);
});

// ----------------------------------------------------------------- Q8.2 CSS

test('Q8.2 cards use an auto-filling CSS grid', () => {
  const rule = ruleFor('.cards');
  assert.match(rule, /display:\s*grid/);
  assert.match(rule, /grid-template-columns:\s*repeat\(\s*auto-fill\s*,\s*minmax\(\s*16rem\s*,\s*1fr\s*\)\s*\)/);
  assert.match(rule, /\bgap:/);
});

test('Q8.2 a focused card gets a visible indicator', () => {
  const rule = ruleFor('.card:focus-within');
  assert.match(rule, /outline:|box-shadow:/);
});

test('Q8.2 the sold-out colour is a custom property used by the badge', () => {
  const root = ruleFor(':root');
  assert.match(root, /--sold-out\s*:/);
  assert.match(ruleFor('.badge-sold-out'), /var\(\s*--sold-out\s*\)/);
});

test('Q8.2 motion is switched off for people who prefer reduced motion', () => {
  assert.match(css, /@media\s*\(\s*prefers-reduced-motion:\s*reduce\s*\)\s*\{[\s\S]*transition:\s*none/);
});

// ------------------------------------------------------------------- Q8.3 JS

test('formatPrice formats whole cents as rand', () => {
  assert.equal(formatPrice(12345), 'R123.45');
  assert.equal(formatPrice(100000), 'R1000.00');
});

test('formatPrice pads the cents to two digits', () => {
  assert.equal(formatPrice(5), 'R0.05');
  assert.equal(formatPrice(9900), 'R99.00');
  assert.equal(formatPrice(0), 'R0.00');
});

const EVENTS = [
  { id: 1, title: 'Jazz Night', city: 'Cape Town', priceCents: 25000, remaining: 40 },
  { id: 2, title: 'Tech Summit', city: 'Johannesburg', priceCents: 89900, remaining: 0 },
  { id: 3, title: 'Cape Comedy Club', city: 'Pretoria', priceCents: 15000, remaining: 12 },
];

test('filterEvents matches title or city, ignoring case', () => {
  assert.deepEqual(filterEvents(EVENTS, 'jazz').map((e) => e.id), [1]);
  assert.deepEqual(filterEvents(EVENTS, 'JOHANNESBURG').map((e) => e.id), [2]);
  assert.deepEqual(filterEvents(EVENTS, 'cape').map((e) => e.id), [1, 3]);
});

test('filterEvents trims the query and returns everything for an empty one', () => {
  assert.equal(filterEvents(EVENTS, '   ').length, 3);
  assert.equal(filterEvents(EVENTS, '').length, 3);
  assert.deepEqual(filterEvents(EVENTS, '  tech  ').map((e) => e.id), [2]);
});

test('filterEvents returns a new array and leaves the input alone', () => {
  const result = filterEvents(EVENTS, '');
  assert.notEqual(result, EVENTS);
  assert.equal(EVENTS.length, 3);
  assert.deepEqual(filterEvents(EVENTS, 'zzz'), []);
});

test('renderEventCards shows a friendly message when there are no events', () => {
  assert.equal(renderEventCards([]), '<p class="empty">No events match your search.</p>');
  assert.equal(renderEventCards(undefined), '<p class="empty">No events match your search.</p>');
});

test('renderEventCards renders one article per event inside a .cards container', () => {
  const out = renderEventCards(EVENTS);
  assert.ok(out.startsWith('<div class="cards">'));
  assert.ok(out.endsWith('</div>'));
  assert.equal((out.match(/<article\b/g) || []).length, 3);
  assert.match(out, /<article class="card" data-id="1">/);
  assert.match(out, /<h3>Jazz Night<\/h3>/);
  assert.match(out, /<p class="city">Cape Town<\/p>/);
  assert.match(out, /<p class="price">R250\.00<\/p>/);
});

test('renderEventCards flags only the sold-out events', () => {
  const out = renderEventCards(EVENTS);
  assert.equal((out.match(/badge-sold-out/g) || []).length, 1);
  assert.match(out, /<span class="badge-sold-out">Sold out<\/span>/);
  const summit = out.split('<article').find((chunk) => chunk.includes('Tech Summit'));
  assert.match(summit, /badge-sold-out/);
});

test('renderEventCards escapes untrusted text', () => {
  const out = renderEventCards([
    { id: 9, title: '<img src=x onerror=alert(1)>', city: 'A & B', priceCents: 100, remaining: 1 },
  ]);
  assert.doesNotMatch(out, /<img/);
  assert.match(out, /&lt;img src=x onerror=alert\(1\)&gt;/);
  assert.match(out, /A &amp; B/);
});

function recordingFetch(response, calls) {
  return async (url, options) => {
    calls.push({ url, options });
    if (response instanceof Error) throw response;
    return response;
  };
}

const PAYLOAD = { eventId: 1, email: 'lerato@example.com', quantity: 2 };

test('submitBooking POSTs the payload as JSON', async () => {
  const calls = [];
  await submitBooking(PAYLOAD, recordingFetch({ ok: true, status: 201, json: async () => ({}) }, calls));
  assert.equal(calls.length, 1);
  assert.equal(calls[0].url, '/api/bookings');
  assert.equal(calls[0].options.method, 'POST');
  assert.equal(calls[0].options.headers['Content-Type'], 'application/json');
  assert.deepEqual(JSON.parse(calls[0].options.body), PAYLOAD);
});

test('submitBooking returns the created booking on 201', async () => {
  const booking = { reference: 'BK-000042' };
  const outcome = await submitBooking(PAYLOAD,
    recordingFetch({ ok: true, status: 201, json: async () => booking }, []));
  assert.deepEqual(outcome, { ok: true, booking });
});

test('submitBooking explains a sold-out event on 409', async () => {
  const outcome = await submitBooking(PAYLOAD, recordingFetch({ ok: false, status: 409 }, []));
  assert.deepEqual(outcome, { ok: false, error: 'Sorry, this event is sold out.' });
});

test('submitBooking asks the user to check their details on 400', async () => {
  const outcome = await submitBooking(PAYLOAD, recordingFetch({ ok: false, status: 400 }, []));
  assert.deepEqual(outcome, { ok: false, error: 'Please check your details and try again.' });
});

test('submitBooking reports other HTTP errors generically', async () => {
  const outcome = await submitBooking(PAYLOAD, recordingFetch({ ok: false, status: 503 }, []));
  assert.deepEqual(outcome, { ok: false, error: 'Something went wrong. Please try again.' });
});

test('submitBooking reports network failures instead of throwing', async () => {
  const outcome = await submitBooking(PAYLOAD, recordingFetch(new TypeError('Failed to fetch'), []));
  assert.deepEqual(outcome, {
    ok: false,
    error: 'Network error. Check your connection and try again.',
  });
});
