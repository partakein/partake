//
// partake-ui.js is a JavaScript library which implements a user interface of PARTAKE.
//

(function() {

    // Exposes PartakeUI.
    function PartakeUI() {
    }

    PartakeUI.prototype = {
        spinner: function(targetElement) {
            var SPINNER_HEIGHT = 15, SPINNER_WIDTH = 15;

            var parent = $(targetElement.parentNode);
            if (!parent.hasClass('spinner-container')) {
                if (window.console)
                    console.log('partakeUI.createSpinner: parent class should have spinner-container class.');
                return null;
            }

            var spinner = $(targetElement.nextElementSibling);
            if (spinner.length == 0 || !spinner.hasClass('spinner')) {
                spinner = document.createElement('img');
                spinner.className = 'spinner';
                spinner.src = "/img/spinner.gif";
                spinner = $(spinner);
                spinner.insertAfter(targetElement);
            }

            var top = $(targetElement).position().top + $(targetElement).outerHeight() / 2 - SPINNER_HEIGHT / 2;
            var left = $(targetElement).position().left + $(targetElement).outerWidth() / 2 - SPINNER_WIDTH / 2;

            spinner.css("position", "absolute");
            spinner.css("top", top);
            spinner.css("left", left);

            return spinner;
        },

        pagination: function(pagination, currentPage, numItems, numItemsPerPage) {
            var links = [];

            function addpagination(text, currentPage, ul, active) {
                if (currentPage < 0 || !currentPage) {
                    if (active)
                        $('<li class="active"><a>' + text + '</a></li>').appendTo(ul);
                    else
                        $('<li class="disabled"><a>' + text + '</a></li>').appendTo(ul);
                    return;
                }

                var li = $('<li></li>');
                var a = $("<a>" + text + "</a>");
                // a.click(function() { update(currentPage); });
                a.appendTo(li);
                li.appendTo(ul);

                links.push({
                    anchor: a,
                    pageNum: currentPage
                });
            }

            pagination.empty();

            var maxPageNum = Math.ceil(numItems / numItemsPerPage);
            if (maxPageNum == 0)
                maxPageNum = 1;

            var ul = $('<ul></ul>');

            // 常に 9 個にしたい
            var beginPage = -1, endPage = -1;
            if (maxPageNum <= 9) {
                beginPage = 1;
                endPage = maxPageNum;
            } else {
                beginPage = currentPage - 2;
                endPage = currentPage + 2;
                if (beginPage < 1) {
                    endPage = endPage + (1 - beginPage);
                    beginPage = 1;
                }
                if (maxPageNum < endPage) {
                    beginPage = beginPage - (endPage - maxPageNum);
                    endPage = maxPageNum;
                }

                if (beginPage == 1)
                    endPage += 2;
                else if (beginPage == 2) {
                    beginPage = 1;
                    endPage += 1;
                }

                if (endPage == maxPageNum)
                    beginPage -= 2;
                else if (endPage + 1 == maxPageNum) {
                    beginPage -= 1;
                    endPage = maxPageNum;
                }
            }

            if (currentPage == 1)
                addpagination('«', 0, ul);
            else
                addpagination('«', currentPage - 1, ul);

            if (beginPage != 1) {
                addpagination(1, 1, ul);
                addpagination('…', 0, ul);
            }

            for (var i = beginPage; i <= endPage; ++i)
                addpagination(i, i == currentPage ? 0 : i, ul, i == currentPage);

            if (endPage != maxPageNum) {
                addpagination('…', 0, ul);
                addpagination(maxPageNum, maxPageNum, ul);
            }

            if (currentPage == maxPageNum)
                addpagination('»', 0, ul);
            else
                addpagination('»', currentPage + 1, ul);

            pagination.append(ul);
            return links;
        }
    };

    // expose partake client to global.
    createPartakeUIClient = function() {
        return new PartakeUI();
    };

})();

exports.createPartakeUIClient = createPartakeUIClient;

