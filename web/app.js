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
//function formatPrice(cents) {
//  // TODO (Q8.3)
//  double rands= cents/100
//  int centss = cents % 100
//   return "R{rands} + . + {centss}"
//   throw new Error('formatPrice is not implemented');
//}

function formatPrice(cents) {
  const rands = Math.floor(cents / 100);
  const centsPart = cents % 100;

  return `R${rands}.${String(centsPart).padStart(2, '0')}`;
}

/**
 * Returns the events whose title or city contains `query`. See the README.
 */
//function filterEvents(events, query) {
//  // TODO (Q8.3)
//  ArrayList freshList = new ArrayList();
//  for (event in events.trim()){
//  if (event.contains("query")
//  freshList.put(event))
//  else(if(event== null){
//  return freshList})}
//  }
//  return freshList;
//  }
//
//  throw new Error('filterEvents is not implemented');
//}

function filterEvents(events, query) {
  const cleanedQuery = query.trim().toLowerCase();

  if (cleanedQuery === '') {
    return [...events];
  }

  return events.filter((event) =>
    event.title.toLowerCase().includes(cleanedQuery) ||
    event.city.toLowerCase().includes(cleanedQuery)
  );
}

/**
 * Builds the HTML for a list of events. See the README for the exact markup.
 */
//function renderEventCards(events) {
//if (events==null){
//return "<p class="empty">No events match your search.</p>"}
//else()
//  // TODO (Q8.3)
//  throw new Error('renderEventCards is not implemented');
//}
function renderEventCards(events) {
  if (!events || events.length === 0) {
    return '<p class="empty">No events match your search.</p>';
  }

  return `
    <div class="cards">
      ${events.map((event) => `
        <article class="card" data-id="${escapeHtml(event.id)}">
          <h3>${escapeHtml(event.title)}</h3>
          <p class="city">${escapeHtml(event.city)}</p>
          <p class="price">${formatPrice(event.price)}</p>
          ${event.remaining === 0
            ? '<span class="badge-sold-out">Sold out</span>'
            : ''}
        </article>
      `).join('')}
    </div>
  `;
}

/**
 * Sends a booking to the API and reports what happened. `fetchFn` is
 * injectable so the function can be tested without a network.
 * See the README for the required requests and return values.
 */
//async function submitBooking(payload, fetchFn = fetch) {
//  // TODO (Q8.3)
//  throw new Error('submitBooking is not implemented');
//}

async function submitBooking(payload, fetchFn = fetch) {
  try {
    const response = await fetchFn('/api/bookings', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    if (response.status === 201) {
      const booking = await response.json();
      return { ok: true, booking };
    }

    if (response.status === 409) {
      return {
        ok: false,
        error: 'Sorry, this event is sold out.'
      };
    }

    if (response.status === 400) {
      return {
        ok: false,
        error: 'Please check your details and try again.'
      };
    }

    return {
      ok: false,
      error: 'Something went wrong. Please try again.'
    };

  } catch (err) {
    return {
      ok: false,
      error: 'Network error. Check your connection and try again.'
    };
  }
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
