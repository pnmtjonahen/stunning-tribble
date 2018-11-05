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
        
        const search = document.getElementById("search").firstChild.textContent;
        console.log(search);
        
        fetch("http://localhost:8088/api/movies?query=" + search).then(res => res.json()).then(json => {

            json.forEach(searchresult => {
                this.searchResultContainer.appendChild(
                        this.replaceTemplateValues(
                                this.searchResultTemplate.cloneNode(true), searchresult));
            });
        });
    }

    replaceTemplateValues(node, blog) {

        Array.from(node.childNodes)
                .filter(n => n.nodeType === Node.TEXT_NODE).forEach(n => {
            let name;
            if ((name = /\{(.*?)\}/.exec(n.nodeValue)) !== null) {
                n.nodeValue = blog[name[1]];
            }
        });

        Array.from(node.childNodes)
                .filter(n => n.nodeType !== Node.TEXT_NODE).forEach(n => {
            Array.from(n.attributes).forEach(attr => {
                Object.keys(blog).forEach(name => {
                    if (attr.value === "{" + name + "}") {
                        attr.value = blog[name];
                    }
                });
            });
            this.replaceTemplateValues(n, blog);
        });

        return node;
    }

}

const view = new IndexView();




