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
                this.searchResultContainer.appendChild(
                        this.replaceTemplateValues(
                                this.searchResultTemplate.cloneNode(true), searchresult));
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

        Array.from(node.getElementsByTagName("img"))
                .filter(img => img.src)
                .forEach(img => {
                    console.log(img);
                });

        return node;
    }

}

const view = new IndexView();




