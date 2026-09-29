'use strict';

/**
 * Returns `text` with & < > " ' replaced by their HTML entities.
 * (Provided - use it for every piece of text that comes from the API.)
 */
function escapeHtml(text) {
  return String(text)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

/**
 * Formats a price given in whole cents as rand: 12345 -> 'R123.45'.
 */
function formatPrice(cents) {
  // TODO (Q8.3)
  throw new Error('formatPrice is not implemented');
}

/**
 * Returns the events whose title or city contains `query`. See the README.
 */
function filterEvents(events, query) {
  // TODO (Q8.3)
  throw new Error('filterEvents is not implemented');
}

/**
 * Builds the HTML for a list of events. See the README for the exact markup.
 */
function renderEventCards(events) {
  // TODO (Q8.3)
  throw new Error('renderEventCards is not implemented');
}

/**
 * Sends a booking to the API and reports what happened. `fetchFn` is
 * injectable so the function can be tested without a network.
 * See the README for the required requests and return values.
 */
async function submitBooking(payload, fetchFn = fetch) {
  // TODO (Q8.3)
  throw new Error('submitBooking is not implemented');
}

// ---- Page wiring (provided - do not change) -------------------------------

if (typeof document !== 'undefined') {
  document.addEventListener('DOMContentLoaded', async () => {
    const searchBox = document.getElementById('search');
    const eventsBox = document.getElementById('events');
    const select = document.getElementById('event-select');
    const form = document.getElementById('booking-form');
    const message = document.getElementById('message');
    let allEvents = [];

    try {
      const response = await fetch('/api/events');
      allEvents = await response.json();
    } catch (err) {
      message.innerHTML = '<p class="error">Could not load events.</p>';
    }

    select.innerHTML = allEvents
      .map((e) => `<option value="${escapeHtml(e.id)}">${escapeHtml(e.title)}</option>`)
      .join('');
    eventsBox.innerHTML = renderEventCards(allEvents);

    searchBox.addEventListener('input', () => {
      eventsBox.innerHTML = renderEventCards(filterEvents(allEvents, searchBox.value));
    });

    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      const data = new FormData(form);
      const outcome = await submitBooking({
        eventId: Number(data.get('eventId')),
        email: data.get('email'),
        quantity: Number(data.get('quantity')),
      });
      message.innerHTML = outcome.ok
        ? `<p>Booked! Your reference is ${escapeHtml(outcome.booking.reference)}.</p>`
        : `<p class="error">${escapeHtml(outcome.error)}</p>`;
    });
  });
}

if (typeof module !== 'undefined' && module.exports) {
  module.exports = { escapeHtml, formatPrice, filterEvents, renderEventCards, submitBooking };
}
