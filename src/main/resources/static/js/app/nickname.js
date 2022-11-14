var main = {
    init: function () {
        var _this = this;

        $('#btn-nickname').on('click', function () {
            var nickname = $('#nickname').val();

            $('#alertNickName').text(nickname);
            $('#nickname_popup').addClass('view');
        });

        $('#alertCancelBtn').on('click', function () {
            $('#nickname_popup').removeClass('view');
        });

        $('#alertOkBtn').on('click', function () {
            _this.save();
        });

        $('#nickname').on('keyup', null, function() {
            var nickname = $('#nickname').val();
            var nmLenght = nickname.length;
            var $nmLength = $('#nmLength');
            var $alertMessage = $('#alertMessage');
            var $startBtn = $('.start_btn')
            var rtnObj = _this.util.validationCheck(nickname);

            $nmLength.text(nmLenght);

            if (!rtnObj.bFlag) {
                _this.util.alertMessage(rtnObj.message);
            } else {
                $alertMessage.text('');
                $nmLength.removeClass('caution')
                $startBtn.addClass('active')
            }
        });
    },
    save: function () {
        var _this = this;
        var nickname = $('#nickname').val();
        var rtnObj = _this.util.validationCheck(nickname);

        if (!rtnObj.bFlag) {
            _this.util.alertMessage(rtnObj.message);
        }

        var data = { nickname: nickname };

        $.ajax({
            type: 'PATCH',
            url: '/api/user/nickname/0',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function(response) {
            if (response.code == 0) {
                $.ajax({
                    type: 'PATCH',
                    url: '/api/user/nickname/1',
                    dataType: 'json',
                    contentType:'application/json; charset=utf-8',
                    data: JSON.stringify(data)
                }).done(function(response) {
                    if (response.code == 0) {
                        window.location.href = '/';
                    } else {
                        $('#nickname_popup').removeClass('view');
                        _this.util.alertMessage(response.message);
                    }
                }).fail(function (error) {
                    alert(JSON.stringify(error));
                });
            } else {
                _this.util.alertMessage(response.message);
            }
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
    util: {
        validationCheck: function(nickname) {
            if (nickname.length < 2 || nickname.length > 10) {
                return {bFlag: false, message: '2자 이상 10자 이하로 입력해주세요.'};
            }

            const regex = /^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]+$/;

            if (!regex.test(nickname)) {
                return {bFlag: false, message: '한글,영문,숫자로 입력해주세요.'};
            }

            return {bFlag: true, message: ''};
        },
        alertMessage: function(message) {
            var $nmLength = $('#nmLength');
            var $startBtn = $('.start_btn')
            var $alertMessage = $('#alertMessage');

            $nmLength.addClass('caution')
            $startBtn.removeClass('active')
            $alertMessage.text(message);
        }
    }
};

main.init();