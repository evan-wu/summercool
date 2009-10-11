AJS.bind("#ellipsis", function ()
{
    var breadcrumbs = AJS.$("breadcrumbs"),
        ellipsis = AJS.$("ellipsis"),
        hiddenCrumbClass = "hidden-crumb";
    try {
        AJS.$$("hidden-crumb", breadcrumbs, function(crumb) {
            AJS.Dom.removeClass(crumb, hiddenCrumbClass);
        });
        AJS.Dom.addClass(ellipsis, hiddenCrumbClass);
    } catch(e) {
        if (typeof console != "undefined") {
            console.log(e);
        }
    }
});