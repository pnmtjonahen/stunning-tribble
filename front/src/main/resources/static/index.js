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
        this.searchResultTemplate = document.getElementById("search-result");
        this.searchResultContainer = this.searchResultTemplate.parentNode;
        this.searchResultTemplate.removeAttribute("id");
        this.clearSearchResult();

        fetch("http://localhost:8088/api/watchlist").then(res => res.json()).then(json => {
            const watchlistContainer = document.getElementById("watchlist");

            json.forEach(wl => {
                watchlistContainer.appendChild(this.newMovieToWatch(wl));
            });
        });
    }

    newMovieToWatch(wl) {
        const p = document.createElement("p");
        p.appendChild(document.createTextNode(wl.title));
        return p;
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
//                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        },
                        method: "POST",
                        body: JSON.stringify(watchmovie)
                    })
                    .then(res => {
//                        document.getElementById('orderstatus').style.display = 'none';
//                        this.clearStatusContainer();
//                        document.getElementById('thankyou').style.display = 'block';
//                        setTimeout(function () {
//                            document.getElementById('thankyou').style.display = 'none';
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

}

const view = new IndexView();




