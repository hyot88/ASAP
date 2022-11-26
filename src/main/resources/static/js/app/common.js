var common = {
    setTemplate: function (template, target, data) {
      var template = $('#' + template + '-template').html();
      template = template.replaceAll('{', '{{').replaceAll('}', '}}')
      Mustache.parse(template);

      var rendered = Mustache.render(template, data);
      $('#' + target).html(rendered).removeClass('hide');
    },
    dateFormat: function (date) {
      return date.substring(2,4) + '-' + date.substring(4,6) + '-' + date.substring(6,8);
    }
};