/* global fetch */
/*jshint esversion: 6 */



class Configuration {
    constructor() {
        this.review = "/api/review";
        this.watchlist = "/api/watchlist";
        this.movies = "/api/movies";
    }
}

const configuration = new Configuration();

class IndexView {
    constructor() {
        this.eventSource;
        this.searchResultContainer = document.getElementById("search-result");
        this.clearSearchResult();

        const reviewContainer = document.getElementById("reviews");
        var status = "open";

        this.eventSource = new EventSource(configuration.review);
        this.eventSource.onmessage = (e) => {
            if (status === "close") {
                this.clearContainer(reviewContainer);
                status = "open";
            }
            var rv = JSON.parse(e.data);
            reviewContainer.appendChild(this.newReview(rv));
            reviewContainer.appendChild(document.createElement("br"));
        };
        this.eventSource.onerror = (e) => {
            this.clearContainer(reviewContainer);
            reviewContainer.appendChild(this.newReview({title:"Error", review:"Reviews not available"}));
            reviewContainer.appendChild(document.createElement("br"));
            status = "close";
            console.table(e);
        };
    }
    
    elementFromHtmlTemplate(htmlTemplate) {
        const template = document.createElement('template');
        template.innerHTML = htmlTemplate;
        return template.content.firstChild;        
    }

    newReview(rv) {
        return this.elementFromHtmlTemplate(
`<div  class="w3-card w3-round w3-white w3-center">
    <div class="w3-container">
        <p>${rv.title}</p>
        <p>${rv.review}</p>
    </div>
</div>`);
    }

    clearContainer(c) {
        while (c.firstChild)
            c.removeChild(c.firstChild);

    }

    clearSearchResult() {
        this.clearContainer(this.searchResultContainer);
    }

    search() {
        this.clearSearchResult();

        const search = document.getElementById("search").value;

        fetch(configuration.movies + "?query=" + search).then(res => res.json()).then(json => {

            json.forEach(searchresult => {
                this.searchResultContainer.appendChild(
                        this.addAction(
                                this.createSearchResult(searchresult)
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
        const btn = this.elementFromHtmlTemplate(
`<button type="button" class="w3-button w3-theme-d1 w3-margin-bottom">
    <i class="fa fa-thumbs-up"></i> Add to watchlist
</button>`);                

        btn.onclick = (e) => {
            //call add watch list with search result
            const watchmovie = {
                id: searchresult.id,
                title: searchresult.title,
                description: searchresult.description,
                watched: "false"
            };

            fetch(configuration.watchlist,
                    {
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        method: "POST",
                        body: JSON.stringify(watchmovie)
                    })
                    .then(res => {
                    })
                    .catch(res => {
                        console.log(res);
                    });
        };
        return btn;
    }

    addReviewButton(searchresult) {
        const btn = this.elementFromHtmlTemplate(
`<button type="button" class="w3-button w3-theme-d1 w3-margin-bottom w3-margin-left">
    <i class="fa fa-comment"></i> Review
</button>`);                

        btn.onclick = () => {
            // update review dialog with title and other info
            var pTitle = document.getElementById("review-title");
            this.clearContainer(pTitle);

            pTitle.appendChild(document.createTextNode(searchresult.title));
            this.openReview(() => {
                const reviewNode = document.getElementById("review-text");
                const review = reviewNode.textContent;
                this.clearContainer(reviewNode);
                const movieReview = {
                    movieId: searchresult.id,
                    title: searchresult.title,
                    review: review
                };

                fetch(configuration.review,
                        {
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            method: "POST",
                            body: JSON.stringify(movieReview)
                        })
                        .then(res => {
                        })
                        .catch(res => {
                            console.log(res);
                        });
                this.closeReview();
            });
        };
        return btn;
    }

    createSearchResult(searchresult) {
        return this.elementFromHtmlTemplate(
`<div  class="w3-container w3-card w3-white w3-round w3-margin"><br>
    <img id="poster" src="${searchresult.poster}" alt="Filmor-serie" class="w3-left w3-margin-right" style="width:60px"/>
    <h4>${searchresult.title}</h4><br>
    <hr class="w3-clear">
    <p>${searchresult.description}</p>
    <img id="backdrop" src="${searchresult.backdrop}" style="width:100%" alt="poster" class="w3-margin-bottom">
</div>`);
    }

    toggelWatchList() {
        var x = document.getElementById("watchlist");
        if (x.className.indexOf("w3-show") === -1) {
            this.clearContainer(x);
            x.className += " w3-show";
            x.previousElementSibling.className += " w3-theme-d1";

            fetch(configuration.watchlist).then(res => res.json()).then(json => {
                json.forEach(wl => {
                    const p = document.createElement("p");
                    p.appendChild(document.createTextNode(wl.title));
                    x.appendChild(p);
                });
            });
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




