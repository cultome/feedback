var serviceEndpoint = "http://localhost:8080";

_.templateSettings = {
  interpolate: /\{\{(.+?)\}\}/g
};

angular.module("Feedback", ["ngResource", "ngSanitize"]);


angular.module("Feedback").controller("RootController", ["$scope", "$sce", function($scope, $sce) {
  "use strict";

  function htmlAdjust(text, maxLen){
    var adjusted = "";
    var words = text.split(" ");
    var lineCount = 0;
    words.forEach(function(word, idx){
      if(lineCount >= maxLen){
        adjusted += "<br>";
        lineCount = 4;
      }
      adjusted += word + " ";
      lineCount += word.length + 1;
    });
    
    return adjusted;
  }

  $scope.changeTitle = function(newTitle){
    $scope.title = $sce.trustAsHtml(htmlAdjust(newTitle, 50));
  };

  $scope.removeAlerts = function(){
    $("#alerts").html("");
  };

  $scope.showAlert = function(msg, alertType, timeout){
    var alertId = "alert_" + new Date().getMilliseconds();
    var alert = _.template('<div class="alert alert-' + alertType + '" role="alert" id="{{id}}">{{message}}</div>');

    if(timeout !== 0){
      setTimeout(function(){
        $("#" + alertId).fadeOut();
      }, timeout || 5000);
    }
    $("#alerts").append(alert({message: msg, id: alertId}));
  };

  $scope.generateFields = function(data){
    var questionGroup = _.template('<div class="form-group question" {{configs}}><label>{{question}}</label>{{field}}</div>');
    var configs = _.template('data-{{name}}="{{value}}"');
    var comboField = _.template('<select class="form-control" name="{{name}}"><option value=""> </option>{{options}}</select>');
    var optionField = _.template('<option value="{{value}}">{{label}}</option>');
    var checkboxField = _.template('<div class="checkbox"><label><input type="checkbox" name="{{name}}" value="{{value}}"> {{label}}</label></div>');
    var radioField = _.template('<div class="radio"><label><input type="radio" name="{{name}}" value="{{value}}"> {{label}}</label></div>');
    var textAreaField = _.template('<textarea class="form-control" name="{{fieldName}}"></textarea>');
    var fields = [];
    
    _.each(data.questions, function(q, qidx){
      var option = "";
      var cfgs;
      
      if(q.optionsType === "textarea"){
        option = textAreaField({fieldName: "q" + qidx});
        
      } else if(q.optionsType === "radio"){
        option = _.map(q.options, function(opt, oidx){
          return radioField({name: "q" + qidx, value: oidx, label: opt.label});
        }).join("");
        
      } else if(q.optionsType === "combo"){
        var options = _.map(q.options, function(opt, oidx){
          return optionField({value: oidx, label: opt.label});
        }).join("");
        
        option = comboField({name: "q" + qidx, options: options});
        
      } else if(q.optionsType === "checkbox"){
        option = _.collect(q.options, function(opt, oidx){
          return checkboxField({name: "q" + qidx, value: oidx, label: opt.label });
        }).join("");
        
      }
      
      cfgs = _.collect(q.config, function(c){ return configs(c); }).join(" ");
      
      fields.push(questionGroup({field: option, question: q.question, configs: cfgs}));
    });
    return fields.join("");   
  };

  $scope.addRestrictions = function(parent){
    $(parent + " input[type='checkbox']").click(function(evt){
      var question = $(evt.currentTarget).parents(".form-group");
      var config = question.data();
      
      if(config.choose){
        var inputName = $(evt.currentTarget).attr("name");
        if($("input[name='" + inputName + "']:checked").size() > config.choose){
          evt.preventDefault();
        }
      }
    });
  };

  $scope.$on("$locationChangeSuccess", function(newState, oldState){
    $scope.removeAlerts();
  });

}]);




















angular.module("Feedback").factory("PollsDao", ['$resource', function($resource) {
  'use strict';

  var PollsResource = $resource(serviceEndpoint + "/polls/:pollId/:actionId", {
    pollId: "@id", actionId: "@actionId"
  });

  return {
    get: function(pollId, onSuccess, onError){
      return PollsResource.get({pollId: pollId}, onSuccess, onError);
    },

    submitAnswers: function(token, responses, onSuccess, onError){
      return PollsResource.save({pollId: token}, responses, onSuccess, onError);
    }
    
  };

}]);




angular.module("Feedback").controller("RespondController", ["$scope", "$location", "$sce", "PollsDao", function($scope, $location, $sce, PollsDao) {
  "use strict";

  $scope.init = function(){
    $scope.currentToken = $location.absUrl().substring($location.absUrl().indexOf("?")+3);
    PollsDao.get($scope.currentToken, function(data){
      $scope.poll = $sce.trustAsHtml($scope.generateFields(data));
      $scope.changeTitle(data.title);
      setTimeout(function(){
        $scope.addRestrictions("#poll");
      }, 500);
    });
  };

  $scope.send = function(){
    checkSubmit(function(){
      PollsDao.submitAnswers($scope.currentToken, JSON.stringify($("form").serializeArray()), function(r){
        $scope.showAlert(r.response, "success", 0);
        $(".main-content").children().remove();
      }, function(error){
        $scope.showAlert(error.responseText, "danger");
      });
    });
  };
  
  function checkSubmit(onSuccess){
    var responses = $("form").serializeArray();
    var questions = $(".question").size();
    for(var i = 0; i < questions; i++){
      if(!isFillUp(responses, i) && !isOptional(i)){
        $scope.showAlert("Answer all the required questions please.", "warning");
        return false;
      }
    }
    return onSuccess();
  }

  function isFillUp(responses, idx){
    return _.any(responses, function(r){ return r.name === ("q" + idx) && !_.isEmpty(r.value); });
  }

  function isOptional(idx){
    return $("[name='q" + idx + "']").parents(".form-group").data("optional");
  }

}]);