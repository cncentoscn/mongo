var baseURl="http://localhost:8080";


//将url变成json数据
function urlToJSON() {
    //url=？xx=yy&ff=jj格式值
    //将url变成json数据
    var k_v = decodeURI(window.location.search).substring(1).split("&");
    var data = {};
    for (var i = 0; i < k_v.length; i++) {
        var arr = k_v[i].split("=");
        data[arr[0]] = arr[1];
    }
    return data;
};


function syntaxHighlight(json) {
    if (typeof json != 'string') {
        json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&').replace(/</g, '<').replace(/>/g, '>');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function(match) {
        var cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
}