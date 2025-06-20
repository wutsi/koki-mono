/*
 Copyright 2016 Google Inc. All Rights Reserved.
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

// The install handler takes care of precaching the resources we always need.

console.log('SW - Registering install');
self.addEventListener('install', event => {
    console.log('SW.install');
    self.skipWaiting()
});

// The activate handler takes care of cleaning up old caches.
console.log('SW - Registering activate');
self.addEventListener('activate', event => {
    console.log('SW.activate');

    return self.clients.claim();
});

// The fetch handler serves responses for same-origin resources from a cache.
// If no response is found, it populates the runtime cache with the response
// from the network before returning it to the page.
console.log('SW - Registering fetch');
self.addEventListener('fetch', event => {
    // console.log('SW.fetch', event.request);
    if (
        event.request.method === 'GET' &&
        (
            event.request.destination === 'image' ||
            event.request.destination === 'script' ||
            event.request.destination === 'style' ||
            event.request.destination === 'manifest' ||
            event.request.destination === 'audio' ||
            event.request.destination === 'video' ||
            event.request.destination === 'font'
        )
    ) {
        const cacheName = _sw_get_cache_name(event);
        // console.log(event.request.url, 'destination=' + event.request.destination, 'cache=' + cacheName);

        // Cache First
        event.respondWith(
            caches.match(event.request)
                .then(cachedResponse => {
                        if (cachedResponse) {
                            return cachedResponse;
                        } else {
                            return fetch(event.request).then((response) => {
                                const cloneResponse = response.clone();
                                caches.open(cacheName).then(cache => {
                                    cache.put(event.request.url, cloneResponse);
                                });
                                return response;
                            });
                        }
                    }
                )
        )
    } else {
        // Network Only
        event.respondWith(
            fetch(event.request)
        );
    }
});

function _sw_get_cache_name(event) {
    if (event.request.destination === 'image' && event.request.url.startsWith('https://tile.openstreetmap.org')) {
        return 'MAP'
    } else if (event.request.destination === 'document') {
        return 'DOCUMENT'
    } else {
        return 'RUNTIME'
    }
}
