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

    }

    newEvent(rv) {
//        const row = document.createElement("div");
//        row.className = "w3-row";
//        const header = document.createElement("div");
//        header.className = "w3-col s1 m1 l1";
//        const pHeader = document.createElement("p");
//        pHeader.appendChild(document.createTextNode(rv.id + " : "));
//        header.appendChild(pHeader);
//        row.appendChild(header);
//        const body = document.createElement("div");
//        body.className = "w3-col s1 m1 l1";
        
        const pre = document.createElement("pre");
        pre.appendChild(document.createTextNode(rv.body));
        const li = document.createElement("li");
        li.appendChild(pre);
        
//        row.appendChild(body);
        return li;
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




