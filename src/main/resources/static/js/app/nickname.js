var main = {
    init: function () {
        var _this = this;

        $('#btn-nickname').on('click', function () {
            _this.save();
        });

        $('#nickname').on('keyup', null, function() {
            var nmLenght = $('#nickname').val().length;
            var $nmLength = $('#nmLength');
            $nmLength.text(nmLenght + " / 10");
            $nmLength.css('color', 'white');

            var $alertMessage = $('#alertMessage');
            $alertMessage.text('');
            $alertMessage.hide();
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
            type: 'POST',
            url: '/api/nicknameCheck',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function(response) {
            if (response.code == 0) {
                if (confirm('"' + nickname + '"(으)로 하시겠습니까?')) {
                    $.ajax({
                        type: 'POST',
                        url: '/api/nickname',
                        dataType: 'json',
                        contentType:'application/json; charset=utf-8',
                        data: JSON.stringify(data)
                    }).done(function(response) {
                        if (response.code == 0) {
                            window.location.href = '/';
                        } else {
                            _this.util.alertMessage(response.message);
                        }
                    }).fail(function (error) {
                        alert(JSON.stringify(error));
                    });
                }
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

            return {bFlag: true};
        },
        alertMessage: function(message) {
            $('#nmLength').css('color', 'red');

            var $alertMessage = $('#alertMessage');
            $alertMessage.text(message);
            $alertMessage.show();
        }
    }
};

main.init();