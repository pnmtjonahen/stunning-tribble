/* global fetch */
/* global config */
/*jshint esversion: 6 */

if (!String.prototype.format) {
    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] !== 'undefined' ? args[number] : match;
        });
    };
}


class Configuration {
    constructor() {
        this.cnf = config;
        this.evUrl = "{0}/api/events";
    }

    eventUrl() {
        return this.evUrl.format(this.cnf.http_server);
    }

}

const configuration = new Configuration();

class IndexView {
    constructor() {
        this.eventSource;
        const eventOutput = document.getElementById("eventoutput");
        this.eventSource = new EventSource(configuration.eventUrl());
        this.eventSource.onmessage = (e) => {
            const ev = this.newEvent(JSON.parse(e.data));
            if (eventOutput.childNodes) {
                eventOutput.insertBefore(ev, eventOutput.childNodes[0]);
            } else {
                eventOutput.appendChild(ev);
            }
            setTimeout(() => {
                ev.classList.toggle("ptj-event-show");
            }, 0);
        };
        this.eventSource.onerror = (e) => {
            console.log(e);
        };

    }

    newEvent(rv) {
        const template = document.createElement('template');
        template.innerHTML = `<li class='ptj-event-hidden'><pre>${rv.body}</pre></li>`;
        return template.content.firstChild;
    }

    onEnterEvent(event) {
        // Cancel the default action, if needed
        event.preventDefault();
        // Number 13 is the "Enter" key on the keyboard
        if (event.keyCode === 13) {
            this.addEvent();
        }
    }

    addEvent() {
        const eventInput = document.getElementById("eventinput");
        const eventBody = {
            body: eventInput.value
        };

        fetch(configuration.eventUrl(),
                {
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    method: "POST",
                    body: JSON.stringify(eventBody)
                })
                .then(res => {
                })
                .catch(res => {
                    console.log(res);
                });
        eventInput.value = "";
    }

}

const view = new IndexView();




