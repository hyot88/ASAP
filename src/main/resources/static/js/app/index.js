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

                var data = {
                     tier: info.tier || 'unranked',
                     nextTier: info.nextTier || '',
                     tierPoint: info.tierPoint || 0,
                     nickname: info.nickname || ''
                };

                common.setTemplate('rankBox', 'rankBox', data)

                if (data.tier === 'unranked') {
                  $('.layout_exp').addClass('hide')
                }
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
                var missionType = response.data.missionType || 0;
                var data = {};

                if (info.length > 0) {
                    var missionBasicInfo = {
                      1: {
                        mission_day: 'Tick 1 Day',
                        mission_day_img: 'mission_day_1'
                      },
                      3: {
                        mission_day: 'Tick 3 Day',
                        mission_day_img: 'mission_day_3'
                      },
                      5: {
                        mission_day: 'Quick 5 Day',
                        mission_day_img: 'mission_day_5'
                      },
                      7: {
                        mission_day: 'Quick 7 Day',
                        mission_day_img: 'mission_day_7'
                      }
                    }

                    data = {
                      missionDay: missionBasicInfo[missionType].mission_day,
                      missionDayImg: missionBasicInfo[missionType].mission_day_img,
                      startDate: common.dateFormat(info[0].date),
                      endDate: common.dateFormat(info[info.length - 1].date)
                    }
                    common.setTemplate('progressMissionBox', 'missionBox', {})
                    common.setTemplate('nowMission', 'nowMission', data)
                } else {
                    common.setTemplate('noProgressMissionBox', 'missionBox', {})
                }

                $('.mission_day').click(function (e) {
                    e.preventDefault()

                    var missionDay = $(this).attr('data-day');
                    if (missionDay) {
                      location.href = '/mission/join/' + missionDay
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
        if (missionType !== undefined) {
            $.ajax({
                type: 'POST',
                url: '/api/mission/' + missionType,
                dataType: 'json',
                contentType:'application/json; charset=utf-8',
            }).done(function(response) {
                if (response.code == 0) {
                    console.log(response)
                    location.href = "/"
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