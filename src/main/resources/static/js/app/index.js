var main = {
    init : function () {
        var _this = this;

        _this.getRankInfo()
        _this.getMission()
    },
    proc : function () {
    },
    getRankInfo: function () {
        $.ajax({
            type: 'GET',
            url: '/api/user',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function(response) {
            if (response.code == 0) {
                console.log(response)
                var info = response.data;

                var template = $('#rankBox-template').html();
                template = template.replaceAll('{', '{{').replaceAll('}', '}}')
                Mustache.parse(template);

                var data = {
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
    },
    getMission: function () {
        $.ajax({
            type: 'GET',
            url: '/api/mission',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function(response) {
            if (response.code == 0) {
                console.log(response)
                var info = response.data.detail || [];
                var template = '';
                var data = {};

                if (info.length > 0) {
                    template = $('#progressMissionBox-template').html();
                } else {
                    template = $('#noProgressMissionBox-template').html();
                }
                Mustache.parse(template);
                var rendered = Mustache.render(template, data);
                $('#missionBox').html(rendered);

                $('#missionBox').removeClass('hide')

                $('.mission_day').click(function (e) {
                    e.preventDefault()
                    main.setMission($(this).attr('data-day'))
                })
            } else {
                _this.util.alertMessage(response.message);
            }
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
    setMission: function (missionType) {
        if (missionType !== undefined && confirm(missionType + 'day Mission을 시작 하시겠습니까?')) {
            $.ajax({
                type: 'POST',
                url: 'api/mission/' + missionType,
                dataType: 'json',
                contentType:'application/json; charset=utf-8',
            }).done(function(response) {
                if (response.code == 0) {
                    console.log(response)
                    main.getMission();
                } else {
                    _this.util.alertMessage(response.message);
                }
            }).fail(function (error) {
                alert(JSON.stringify(error));
            });
        }
    }
};

main.init();