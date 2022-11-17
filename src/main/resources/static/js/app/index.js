var main = {
    init : function () {
        var _this = this;

        _this.getRankInfo()
        _this.getMission()

        $('#alertCancelBtn').on('click', function () {
            $('#mission_popup').removeClass('view');
        });

        $('#alertOkBtn').on('click', function () {
            _this.setMission($('#missionDay').text());
        });
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
                     tier: info.tier || 'unranked',
                     nextTier: info.nextTier || '',
                     tierPoint: info.tierPoint || 0,
                     nickname: info.nickname || ''
                };

                var rendered = Mustache.render(template, data);
                $('#rankBox').html(rendered);

                if (data.tier === 'unranked') {
                  $('.layout_exp').addClass('hide')
                }

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

                    var missionDay = $(this).attr('data-day');
                    if (missionDay) {
                      $('#missionDay').text(missionDay);
                      $('#mission_popup').addClass('view');
                    }
                })
            } else {
                _this.util.alertMessage(response.message);
            }
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
    setMission: function (missionType) {
        $('#mission_popup').removeClass('view');
        if (missionType !== undefined && missionType.trim() !== '') {
            $.ajax({
                type: 'POST',
                url: 'api/mission/' + missionType,
                dataType: 'json',
                contentType:'application/json; charset=utf-8',
            }).done(function(response) {
                if (response.code == 0) {
                    console.log(response)
                    main.getRankInfo()
                    main.getMission()
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