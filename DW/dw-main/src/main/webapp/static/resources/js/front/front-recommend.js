$(function () {

    var recommendContainer = $("#recommendContainer")[0];
    var recomemend = $("#recommend")[0];
    var top = recommendContainer.getBoundingClientRect().top + ($(document).scrollTop()?$(document).scrollTop():$("#outerContainer").scrollTop());

    $(document).on("scroll", function (e) {
        //滚动限制
        var maxScrollTop = recommendContainer.getBoundingClientRect().height + top - document.documentElement.clientHeight;

        if(maxScrollTop < 0){
            // $(recomemend).css("position","fixed");
            $(recomemend).css("top",top)
        }else  if ($(document).scrollTop() >= maxScrollTop) {
            $(recommendContainer).addClass("noScroll");
        } else {
            $(recommendContainer).removeClass("noScroll");
        }
    });
    $("#outerContainer").on("scroll", function (e) {
        var maxScrollTop = recommendContainer.getBoundingClientRect().height + top - document.documentElement.clientHeight;

        if(maxScrollTop < 0){
            // $(recomemend).css("position","fixed");
            $(recomemend).css("top",top)
        }else  if ($("#outerContainer").scrollTop() >= maxScrollTop) {
            $(recommendContainer).addClass("noScroll");
        } else {
            $(recommendContainer).removeClass("noScroll");

        }


    });

});