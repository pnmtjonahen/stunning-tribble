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
            var rv = JSON.parse(e.data);
            if (eventOutput.childNodes) {
                eventOutput.insertBefore(this.newEvent(rv), eventOutput.childNodes[0]);
            } else {
                eventOutput.appendChild(this.newEvent(rv));
            }
        };
        this.eventSource.onerror = (e) => {
            console.log(e);
        };

        this.addEventButton();
    }

    newEvent(rv) {
        const container = document.createElement("li");
        container.appendChild(document.createTextNode(rv.id + " : "));
        container.appendChild(document.createTextNode(rv.msgBody));
        return container;
    }

    addEventButton() {
        const btn = document.getElementById("send");
        btn.onclick = () => {
            const eventInput = document.getElementById("eventinput");
            const eventBody = {
                msgFrom: 'sample',
                msgBody: eventInput.value
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
        };
    }

}

const view = new IndexView();




