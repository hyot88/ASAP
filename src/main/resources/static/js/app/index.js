var main = {
    init : function () {
        var _this = this;

        _this.getRankInfo()
    },
    proc : function () {
    },
    getRankInfo: function () {
        $.ajax({
            type: 'GET',
            url: '/api/user/rank/0',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function(response) {
            if (response.code == 0) {
                console.log(response)
                var info = response.data[0];

                var template = $('#rankBox-template').html();
                template = template.replaceAll('{', '{{').replaceAll('}', '}}')
                Mustache.parse(template);

                var data = {
                	 ranking: info.ranking || 'UNRANKED',
                     tier: info.tier || 0,
                     tierPoint: info.tierPoint || 0,
                     nickname: info.nickname || ''
                };

                var rendered = Mustache.render(template, data);
                $('#rankBox').html(rendered);

                $('#rankBox').removeClass('hide')
            } else {
                _this.util.alertMessage(response.message);
            }
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });

    }
};

main.init();