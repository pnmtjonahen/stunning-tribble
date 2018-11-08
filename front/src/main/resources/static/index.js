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




class IndexView {
    constructor() {
        this.eventSource;
        this.searchResultTemplate = document.getElementById("search-result");
        this.searchResultContainer = this.searchResultTemplate.parentNode;
        this.searchResultTemplate.removeAttribute("id");
        this.clearSearchResult();
        const reviewContainer = document.getElementById("reviews");

        this.eventSource = new EventSource("http://localhost:8088/api/reviews");
        this.eventSource.onmessage = (e) => {
            var rv = JSON.parse(e.data);
            reviewContainer.appendChild(this.newReview(rv));
            reviewContainer.appendChild(document.createElement("br"));
        };
        this.eventSource.onerror = (e) => {
            console.log(e);
        };
    }

    newReview(rv) {
        const container = document.createElement("div");
        container.className = " w3-card w3-round w3-white w3-center";

        const div = document.createElement("div");
        div.className = "w3-container";
        const pTitle = document.createElement("p");
        pTitle.appendChild(document.createTextNode(rv.title));
        div.appendChild(pTitle);
        const pReview = document.createElement("p");
        pReview.appendChild(document.createTextNode(rv.review));
        div.appendChild(pReview);

        container.appendChild(div);
        return container;
    }
    clearSearchResult() {
        while (this.searchResultContainer.firstChild)
            this.searchResultContainer.removeChild(this.searchResultContainer.firstChild);
    }

    search() {
        this.clearSearchResult();

        const search = document.getElementById("search").value;

        fetch("http://localhost:8088/api/movies?query=" + search).then(res => res.json()).then(json => {

            json.forEach(searchresult => {
                this.searchResultContainer.appendChild(this.addAction(
                        this.replaceTemplateValues(
                                this.searchResultTemplate.cloneNode(true)
                                , searchresult)
                        , searchresult)
                        );
            });
        });
    }

    onSearch(event) {
        // Cancel the default action, if needed
        event.preventDefault();
        // Number 13 is the "Enter" key on the keyboard
        if (event.keyCode === 13) {
            this.search();
        }
    }

    addAction(node, searchresult) {
        node.appendChild(this.addWatchListButton(searchresult));
        node.appendChild(this.addReviewButton(searchresult));
        return node;
    }

    addWatchListButton(searchresult) {
        const btn = document.createElement("button");
        btn.type = "button";
        btn.className = "w3-button w3-theme-d1 w3-margin-bottom";
        const i = document.createElement("i");
        i.className = "fa fa-thumbs-up";
        btn.appendChild(i);
        btn.appendChild(document.createTextNode(" Add to watchlist"));
        btn.onclick = (e) => {
            //call add watch list with search result
            const watchmovie = {
                id: searchresult.id,
                title: searchresult.title,
                description: searchresult.description,
                watched: "false"
            };

            fetch("http://localhost:8088/api/watchlist",
                    {
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        method: "POST",
                        body: JSON.stringify(watchmovie)
                    })
                    .then(res => {
//                        document.getElementById('added').style.display = 'block';
//                        setTimeout(function () {
//                            document.getElementById('added').style.display = 'none';
//                        }, 2000);
                    })
                    .catch(res => {
                        console.log(res);
                    });
        };
        return btn;
    }

    addReviewButton(searchresult) {
        const btn = document.createElement("button");
        btn.type = "button";
        btn.className = "w3-button w3-theme-d1 w3-margin-bottom w3-margin-left";
        const i = document.createElement("i");
        i.className = "fa fa-comment";
        btn.appendChild(i);
        btn.appendChild(document.createTextNode(" Review"));
        btn.onclick = () => {
            // update review dialog with title and other info
            var pTitle = document.getElementById("review-title");
            while (pTitle.firstChild)
                pTitle.removeChild(pTitle.firstChild);

            pTitle.appendChild(document.createTextNode(searchresult.title));
            this.openReview(() => {
                const movieReview = {
                    movieId: searchresult.id,
                    title: searchresult.title,
                    review: "Lorum ipsum....."
                };

                fetch("http://localhost:8088/api/reviews",
                        {
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            method: "POST",
                            body: JSON.stringify(movieReview)
                        })
                        .then(res => {
//                        document.getElementById('added').style.display = 'block';
//                        setTimeout(function () {
//                            document.getElementById('added').style.display = 'none';
//                        }, 2000);
                        })
                        .catch(res => {
                            console.log(res);
                        });
                this.closeReview();
            });
        };
        return btn;
    }

    replaceTemplateValues(node, searchresult) {

        Array.from(node.childNodes)
                .filter(n => n.nodeType === Node.TEXT_NODE).forEach(n => {
            let name;
            if ((name = /\{(.*?)\}/.exec(n.nodeValue)) !== null) {
                n.nodeValue = searchresult[name[1]];
            }
        });

        Array.from(node.childNodes)
                .filter(n => n.nodeType !== Node.TEXT_NODE)
                .forEach(n => {
                    if (n.attributes && n.attributes.length > 0) {
                        Array.from(n.attributes).forEach(attr => {
                            Object.keys(searchresult).forEach(name => {
                                if (attr.value === "{" + name + "}") {
                                    attr.value = searchresult[name];
                                }
                            });
                        });
                    }
                    this.replaceTemplateValues(n, searchresult);
                });

        return node;
    }

    toggelWatchList(id) {
        var x = document.getElementById(id);
        if (x.className.indexOf("w3-show") === -1) {
            x.className += " w3-show";
            x.previousElementSibling.className += " w3-theme-d1";
        } else {
            x.className = x.className.replace("w3-show", "");
            x.previousElementSibling.className =
                    x.previousElementSibling.className.replace(" w3-theme-d1", "");
        }
    }
    openReview(onclick) {
        document.getElementById("sumbit-review").onclick = onclick;
        document.getElementById('new-review').style.display = 'block';
    }
    closeReview() {
        document.getElementById('new-review').style.display = 'none';
    }

}

const view = new IndexView();




