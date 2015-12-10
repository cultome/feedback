var serviceEndpoint = "http://localhost:8080";

_.templateSettings = {
  interpolate: /\{\{(.+?)\}\}/g
};

angular
  .module("Feedback", ["ngRoute", "ngResource", "ngSanitize"/*,"templates"*/])
  .config(["$routeProvider", function($routeProvider) {
    "use strict";

    $routeProvider
      .when("/admin/answers", {templateUrl: "templates/answers.html", controller: "AnswersController"})
      .when("/admin/edit", {templateUrl: "templates/edit.html", controller: "EditController"})
      .when("/admin/list", {templateUrl: "templates/list.html", controller: "ListController"})
      .when("/admin/new", {templateUrl: "templates/new.html", controller: "NewController"})
      .otherwise({redirectTo: "/admin/list"});
}]);

















angular.module("Feedback").controller("RootController", ["$scope", "$http", "$location", "$sce", "AdminDao", function($scope, $http, $location, $sce, AdminDao) {
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

  $scope.$on("$locationChangeStart", function(evt, newUrl, oldUrl){
    $scope.pendingUrl = newUrl.substring(newUrl.indexOf("#") + 1);

    if(_.isNull(AdminDao.getToken())){
      evt.preventDefault();

      setTimeout(function(){
        $("#login").modal("show");
      }, 300);
    }
  });

  $scope.$on("$locationChangeSuccess", function(evt, newState, oldState){
    $scope.removeAlerts();
  });

  $scope.$on("login", function(evt, token, username){
    AdminDao.setToken(token);

    $http.defaults.headers.common.token = token;
    
    if($scope.pendingUrl){
      if($scope.pendingUrl.indexOf("?") > 0){
        $location.url($scope.pendingUrl + "&refresh=true");
      } else {
        $location.url($scope.pendingUrl + "/");
      }

    }
  });

}]);
















angular.module("Feedback").factory("AdminDao", ['$resource', function($resource) {
  'use strict';

  var token = null;

  var AdminResource = $resource(serviceEndpoint + "/admin/:actionId", {
    actionId: "@actionId"
  });

  return {
    login: function(username, password, onSuccess, onError){
      return AdminResource.save({}, {username: username, password: password}, onSuccess, onError);
    },

    setToken: function(tkn){
      token = tkn;
    },

    getToken: function(){
      return token;
    },
  };
}]);



























angular.module("Feedback").factory("Editor", [function() {
  "use strict";

  var tags = [
    "Title",
    "Question",
    "Config",
    "SendTo",
    "EmailSubject",
    "EmailTitle",
    "EmailContent",
    "EmailSign"
  ];
  
  CodeMirror.defineSimpleMode("feedback", {
    start: [
      {
        regex: /([\s]*)({[\s]*)([\S]+)([\s]+)([\S]+)([\s]*)(=>)([\s]*)([\S]+)([\s]*})(.*)/, 
        token: [null, "control-char", "directive", null, "param-name", null, "control-char", null, "param-value", "control-char", "data"],
        sol: true
      },
      {
        regex: /([\s]*)({[\s]*)([\S]+)([\s]*})(.*)/, 
        token: [null, "control-char", "directive", null, "data"],
        sol: true
      },
      {
        regex: /([\s]*)(\([\s]*\)|\[[\s]*\]|->|[_]+)(.*)/, 
        token: [null, "option", "data"],
        sol: true,
      },
      {
        regex: /(.*)/, 
        token: ["data"],
        sol: true,
      }
    ]
  });

  CodeMirror.registerHelper("hint", "feedback", function(editor, options){
    var line = editor.getLine(editor.getCursor().line);
    var idx = 0;
    var filter = "";
    var prevLine;

    if(/\{.*?\}/.exec(line)){
      return null;
    }

    if(editor.getCursor().line > 0){
      prevLine = editor.getLine(editor.getCursor().line-1);
      if(prevLine.match(/\{Question\}/i) || prevLine.match(/\{Config\}/i) || prevLine.match(/([\s]*)(\([\s]*\)|\[[\s]*\]|->|[_]+)(.*)/)){
        var optList = ["() ", "[] ", "-> ", "____"];

        if(prevLine.match(/([\s]*)\([\s]*\)(.*)/)){
          optList = ["() "];
        } else if(prevLine.match(/([\s]*)\[[\s]*\](.*)/)){
          optList = ["[] "];
        } else if(prevLine.match(/([\s]*)->(.*)/)){
          optList = ["-> "];
        } else if(prevLine.match(/([\s]*)[_]+(.*)/)){
          return null;
        }

        // dont autocomplete twice the same line
        if(line.match(/([\s]*)(\([\s]*\)|\[[\s]*\]|->|[_]+)(.*)/)){
          return null;
        }

        return {
          list: optList,
          from: CodeMirror.Pos(editor.getCursor().line, 0),
          to: CodeMirror.Pos(editor.getCursor().line, 0),
        };
      }
    }

    if(/^[\s]*\{[^}]*$/.exec(line)){
      idx = line.indexOf("{");
      if(idx < line.length-1){
        filter = line.substring(idx+1);
      }
    } else if(/^[^{ ]*$/.exec(line)){
      idx = 0;
      filter = line;
    } else if(/^[\w]+ [\w]+$/.exec(line)){
      return null;
    }

    var r = {
      list: _.chain(tags)
        .map(function(t){
          if(filter.length > 0){
            if(new RegExp("^" + filter, "i").exec(t)){
              return "{" + t + "} ";
            }
          } else {
            return "{" + t + "} ";
          }
        })
        .filter(_.negate(_.isUndefined))
        .value(),
      from: CodeMirror.Pos(editor.getCursor().line, idx),
      to: CodeMirror.Pos(editor.getCursor().line, idx+1+filter.length),
    };

    return r;
  });

  
  return {
    editor: null,
    init: function(){
      this.editor = CodeMirror.fromTextArea(document.getElementById("script"), {
        lineNumbers: false,
        mode: "feedback",
        matchBrackets: true,
        styleActiveLine: true,
        extraKeys: {"Ctrl-Space": "autocomplete"}
      });
      
      this.editor.on("inputRead", function(editor, change) {
        if (change.text[0] === "{"){
          editor.showHint();
        }
      });
    }
  };
}]);







angular.module("Feedback").factory("AdminPollDao", ["$resource", function($resource) {
  "use strict";

  var AdminPollsResource = $resource(serviceEndpoint + "/polls/admin/:pollId/:actionId", {
    pollId: "@id", actionId: "@actionId"
  });

  var checkAuthError = function(defaultCb){
    return function(response,headers){
      if(response.status === 403){
        $("#login").modal("show");
      } else {
        if(_.isFunction(defaultCb)){
          defaultCb(response);
        }
      }
    };
  };

  return {
    list: function(onSuccess, onError){
      return AdminPollsResource.query({}, checkAuthError(onSuccess), checkAuthError(onError));
    },

    create: function(script, onSuccess, onError){
      return AdminPollsResource.save({script: script}, checkAuthError(onSuccess), checkAuthError(onError));
    },

    updatePreview: function(script, onSuccess, onError){
      return AdminPollsResource.save({actionId: "preview"}, {script: script}, checkAuthError(onSuccess), checkAuthError(onError));
    },

    get: function(pollId, onSuccess, onError){
      return AdminPollsResource.get({pollId: pollId}, checkAuthError(onSuccess), checkAuthError(onError));
    },

    update: function(pollId, script, onSuccess, onError){
      return AdminPollsResource.save({pollId: pollId, actionId: "update"}, {script: script}, checkAuthError(onSuccess), checkAuthError(onError));
    },

    send: function(pollId, onSuccess, onError){
      return AdminPollsResource.save({pollId: pollId, actionId: "send"}, {}, checkAuthError(onSuccess), checkAuthError(onError));
    },

    pollAnswers: function(pollId, onSuccess, onError){
      return AdminPollsResource.get({pollId: pollId, actionId: "answers"}, {}, checkAuthError(onSuccess), checkAuthError(onError));
    },

    "delete": function(pollId, onSuccess, onError){
      return AdminPollsResource.delete({pollId: pollId}, checkAuthError(onSuccess), checkAuthError(onError));
    }
  };
}]);



































angular.module("Feedback").controller("AnswersController", ["$scope", "$sce", "$location", "AdminPollDao", function($scope, $sce, $location, AdminPollDao) {
  "use strict";

  $scope.currentView = "summary";

  $scope.init = function(){
    AdminPollDao.pollAnswers($location.search().p, function(data){
      $scope.currentPoll = data;
      $scope.changeTitle("Answers for poll \"" + data.title + "\"");
      $scope.switchToSummaryView();
    }, function(){
      $scope.showAlert("And error ocurred! Please try again.", "danger", 5000);
    });
  };

  $scope.update = function(){
    AdminPollDao.pollAnswers($location.search().p, function(data){
      $scope.currentPoll = data;
      if($scope.currentView === "summary"){
        $scope.switchToSummaryView();
      } else if($scope.currentView === "table") {
        $scope.switchToTableView();
      } else if($scope.currentView === "graph") {
        $scope.switchToGraphView();
      }
    }, function(){
      $scope.showAlert("And error ocurred! Please try again.", "danger", 5000);
    });
  };

  $scope.switchToSummaryView = function(){
    $scope.answers = $sce.trustAsHtml(generateSummary($scope.currentPoll));
    $scope.currentView = "summary";
  };

  $scope.switchToTableView = function(){
    var table = _.template('<table class="table table-hover table-bordered table-condensed"><thead>{{header}}</thead><tbody>{{body}}</tbody></table>');
    $scope.answers = $sce.trustAsHtml(table({header: getTableHeader(), body: getTableBody()}));
    $scope.currentView = "table";
  };

  $scope.switchToGraphView = function(){
    $scope.answers = $sce.trustAsHtml('<div id="graph"><canvas id="chart" width="500px" height="500px"></canvas><div id="legend-panel"></div></div>');
    var options = {
      legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li data-index=\"<%= i %>\" class=\"legend\" style=\"border-color: <%=datasets[i].strokeColor%>\"><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>"
    };

    setTimeout(function(){
      var ctx = document.getElementById("chart").getContext("2d");
      var radar = new Chart(ctx).Radar(getGraphData(), options);

      $("#graph #legend-panel").html(radar.generateLegend());

      $(".legend").mouseover(function(evt){
        var idx = parseInt($(this).data("index"));
        radar.datasets[idx].prevFill = radar.datasets[idx].fillColor;
        radar.datasets[idx].fillColor = radar.datasets[idx].prevFill.replace("0.1", "1.0");
        radar.update();
      });

      $(".legend").mouseout(function(evt){
        var idx = parseInt($(this).data("index"));
        radar.datasets[idx].fillColor = radar.datasets[idx].prevFill.replace("1.0", "0.1");
        radar.update();
      });
    }, 100);

    $scope.currentView = "graph";
  };

  function getDatasetsLabels(idx){
    return _.map($scope.currentPoll.questions[idx].options, function(opt){ return opt.label; });
  }

  function getDatasetsPoints(groupByIdx, answersToQuantify){
    var groupOne = _.groupBy($scope.currentPoll.answers, function(a){
      return a.txId + ":" + a.email;
    });

    var values = _.values(groupOne);

    var points = _.reduce(values, function(acc, answers){
      // find dthe grouping criteria
      var groupCriteria = _.find(answers, function(a){ return a.questionIdx === groupByIdx; });

      if(_.isUndefined(acc[groupCriteria.answer])){
        acc[groupCriteria.answer] = {count: 0};
        _.each(answersToQuantify, function(answer){
          acc[groupCriteria.answer][answer.id.toString()] = 0;
        });
      }

      acc[groupCriteria.answer].count = acc[groupCriteria.answer].count + 1;

      _.each(answers, function(a){
        var question = _.find(answersToQuantify, function(ans){ return ans.id === a.questionIdx; });
        if(!_.isUndefined(question)){
          var factor;

          if(isOrderAscending(question.config)){
            factor = (100 / (_.size(question.options) - 1)) * parseInt(a.answer);
          } else {
            factor = 100 - (100 / (_.size(question.options) - 1)) * parseInt(a.answer);
          }

          acc[groupCriteria.answer][a.questionIdx.toString()] = acc[groupCriteria.answer][a.questionIdx.toString()] + factor;
        }
      });

      return acc;
    }, {});

    // obtain the average
    var averagedPoints = _.map(points, function(p){
      var avg = _.mapObject(p, function(v, k){
        return v / p.count;
      });
      delete avg.count;
      return avg;
    });

    return averagedPoints;
  }

  function isOrderAscending(config){
    var orderConfig = _.find(config, function(c){ return c.name === "order"; });
    var isAscending = true;

    if(!_.isUndefined(orderConfig)){
      isAscending = orderConfig.value === "ascending";
    }
    return isAscending;
  }

  function getGraphData(){
    var axisQuestions = _.select($scope.currentPoll.questions, function(question, idx){
      var q = _.find(question.config, function(c){ return c.name === "axis"; });
      if(_.isUndefined(q)){
        return false;
      }
      question.id = idx;
      return true;
    });

    var labelQuestion = _.find($scope.currentPoll.questions, function(question, idx){
      var q = _.find(question.config, function(c){ return c.name === "labels" && c.value === "true"; });
      if(_.isUndefined(q)){
        return false;
      }
      question.id = idx;
      return true;
    });

    var labels = _.flatten(_.map(labelQuestion.options, function(o){ return o.label; }));

    var axis = _.map(axisQuestions, function(q){
        return _.find(q.config, function(c){ return c.name === "axis";}).value;
    });

    var colors = _.first([
          "rgba(253, 153, 153,1.0)",
          "rgba(222, 222, 34,1.0)",
          "rgba(255,200,255,1.0)",
          "rgba(139, 195, 139,1.0)",
          "rgba(147, 216, 147,1.0)"], _.size(labels));

    var data = getDatasetsPoints(labelQuestion.id, axisQuestions);

    var datasets = _.compact(_.collect(_.zip(labels, colors, _.values(data)), function(a){
      if(_.isUndefined(a[2])){
        return null;
      }

      return {
          label: a[0],
          fillColor: a[1].replace("1.0", "0.1"),
          strokeColor: a[1],
          pointColor: a[1],
          pointHighlightStroke: a[1],
          pointStrokeColor: "#fff",
          pointHighlightFill: "#fff",
          data: _.values(a[2])
      };
    }));

    return {
      labels: axis,
      datasets: datasets
    };
  }

  function getOptionText(questionId, answerId){
    var question = $scope.currentPoll.questions[questionId];
    if(question.optionsType === "textarea"){
      return answerId;
    } else {
      if(!_.isEmpty(answerId)){
        return question.options[answerId].label;
      }
    }

    return "";
  }

  function getTableBody(){
    var responseRow = _.template('<tr><td class="email">{{email}}</td>{{responses}}</tr>');
    var response = _.template('<td>{{response}}</td>');
    // agrupamos por email
    var byEmail = _.groupBy($scope.currentPoll.answers, function(a){ return a.email; });
    var body = _.collect(byEmail, function(answers,email){
      // agrupamos por transaccion
      var byTxId = _.groupBy(answers, function(a){ return a.txId; });
      // filas de un usuario
      var rows = _.collect(byTxId, function(ans, txId){
        // agrupamos por pregunta
        var byQId = _.groupBy(ans, function(a){ return a.questionIdx; });
        var idx = 0;
        // columnas de una transaccion
        var cols = _.collect(byQId, function(s,qId){
          var res = "";
          // si hay respuestas sin contestar, llenamos la columna vacia
          while(idx < s[0].questionIdx){
            res += "<td></td>";
            idx += 1;
          }
          idx += 1;

          if(s.length === 1){
            // si la respuesta en unica
            return res + response({response: getOptionText(s[0].questionIdx, s[0].answer) });
          } else {
            // si hay mas de una respuesta (checkboxes)
            var collection = _.collect(s, function(r){
              return "<li>" + getOptionText(r.questionIdx, r.answer) + "</li>";
            }).join("");
            return res + response({response: collection });
          }
        }).join("");
        return responseRow({idx: txId, email: email, responses: cols});

      });

      // agrupamos las respuestas del mismo usuario
      if(rows.length > 1){
        var isFirst = true;
        rows = _.collect(rows, function(row, idx){
          if(isFirst){
            row = row.replace("<td class=\"email\">", "<td class=\"email\" rowspan=\"" + rows.length + "\">");
            isFirst = false;
          } else {
            row = row.replace(/<td class="email">[^<]+<\/td>/, "");
          }
          return row;
        });
      }

      return rows.join(" ");
    }).join("");

    return body;

  }

  function getTableHeader(){
    var headerRow = _.template('<tr><th>E-Mail</th>{{headers}}</tr>');
    var header = _.template('<th>{{question}}</th>');
    
    var titles = _.collect($scope.currentPoll.questions, function(q){
      return header({question: q.question});
    }).join("");

    return headerRow({headers: titles});
  }
  
  function generateSummary(poll){
    var question = _.template('<label>{{text}}</label><ul class="list-group">{{options}}</ul>');
    
    return _.collect(poll.questions, function(q, idx){
      var answers = _.filter(poll.answers, function(ans){
        return ans.questionIdx === idx;
      });
      
      if(q.optionsType === "radio" || q.optionsType === "checkbox" || q.optionsType === "combo" ){
        return question({text: q.question, options: selectOptions(q, answers)});
      } else if(q.optionsType === "textarea"){
        return question({text: q.question, options: textOptions(q, answers)});
      }
    }).join(" ");
  }
  
  function textOptions(question, answers){
    var options = _.template('<li class="list-group-item">{{text}}</li>');
    
    return _.collect(answers, function(ans, idx){
      return options({text: ans.answer});
    }).join("");
  }
  
  function selectOptions(question, answers){
    var options = _.template('<li class="list-group-item"><span class="badge">{{count}}</span> {{option}}</li>');
    
    var counters = _.countBy(answers, function(ans){
      return ans.answer;
    });
    
    return _.collect(question.options, function(opt, idx){
      return options({option: opt.label, count: counters[idx + ""]});
    }).join("");
  }

}]);

















angular.module("Feedback").controller("NewController", ["$scope", "$location", "AdminPollDao", "Editor",  function($scope, $location, AdminPollDao, Editor) {
  "use strict";

  $scope.create = function(){
    AdminPollDao.create(Editor.editor.getValue(), function(){
      $location.path("/admin/list");
      setTimeout(function(){
        $scope.showAlert("Poll created!", "success");
      }, 500);
    }, function(error){
      $scope.showAlert(error.data.response, "danger");
    });
  };
}]);
















angular.module("Feedback").controller("EditController", ["$scope", "$location", "AdminPollDao", "Editor", function($scope, $location, AdminPollDao, Editor) {
  "use strict";

  $scope.init = function(){
    AdminPollDao.get($location.search().p, function(poll){
      $scope.currentPoll = poll;
      Editor.editor.setValue($scope.currentPoll.script);
      $scope.$broadcast("updatePreview");

      if(!$scope.isEditable()){
        $scope.showAlert("<b>READ ONLY!</b> Poll sent " + new Date($scope.currentPoll.sent).toLocaleString(), "warning", 0);
      }
    });
  };

  $scope.update = function(){
    AdminPollDao.update($scope.currentPoll.id, Editor.editor.getValue(), function(data){
      $scope.showAlert(data.response, "success");
    }, function(error){
      $scope.showAlert(error.data.response, "danger");
    });
  };

  $scope.send = function(){
    AdminPollDao.send($scope.currentPoll.id, function(data){
      $("#confirm").modal('hide');
      $scope.showAlert(data.response, "success");
      $scope.currentPoll.sent = new Date().getTime();
    }, function(error){
      $scope.showAlert(error.data.response, "danger");
    });
  };

  $scope.isEditable = function(){
    return !_.isUndefined($scope.currentPoll) && _.isNull($scope.currentPoll.sent);
  };

}]);

















angular.module("Feedback").controller("ListController", ["$scope", "AdminPollDao", function($scope, AdminPollDao) {
  "use strict";

  $scope.toDelete = 0;

  $scope.init = function(){
    $scope.polls = AdminPollDao.list();
  };

  $scope.getStatusType = function(sentDate){
    return _.isNull(sentDate) ? "warning" : "info";
  };

  $scope.getStatusText = function(sentDate){
    return _.isNull(sentDate) ? "Not sended" : "Sent: " + new Date(sentDate).toLocaleString();
  };

  $scope.prepareDeletion = function(id){
    $scope.toDelete = id;
  };

  $scope.delete = function(){
    AdminPollDao.delete($scope.toDelete, function(){
      $scope.polls = _.filter($scope.polls, function(p){
        return p.id !== $scope.toDelete;
      });
    });

    $("#confirm").modal("hide");
  };
}]);














angular.module("Feedback").directive("editPanel", ["$sce", "AdminPollDao", "Editor", function($sce, AdminPollDao, Editor) {
  "use strict";

  return {
    replace: true,
    templateUrl: "templates/editPanel.html",
    scope: {
      enabled: "&"
    },
    link: function(scope, elem, attr){

      scope.isEnabled = _.isFunction(scope.enabled()) ? scope.enabled() : function(){ return true; };

      scope.updatePreview = function(){
        scope.$broadcast("updatePreview");
      };

      scope.hasEmailTags = function(){
        return _.isString(scope.subject) || _.isString(scope.sign) || _.isString(scope.emailTitle) || (!_.isNull(scope.content) && !_.isUndefined(scope.content));
      };
      
      Editor.init();

      scope.$on("updatePreview", function(evt){

        AdminPollDao.updatePreview(Editor.editor.getValue(), function(data){
          scope.title = data.title;
          scope.subject = data.emailSubject;
          scope.sign = data.emailSign;
          scope.emailTitle = data.emailTitle;
          if(_.isString(data.emailContent)){
            scope.content = $sce.trustAsHtml(data.emailContent.replace(/<<([^>]+)>>/gm, "<a href=\"#\"><span style=\"font-size: 14px; font-family: 'Arial', sans-serif; color: #0070C0\"><b>$1</b></span></a>").replace(/\n/g, "<br>"));
          } else {
            scope.content = null;
          }

          scope.preview = $sce.trustAsHtml(scope.$parent.generateFields(data));
          setTimeout(function(){
            scope.$parent.addRestrictions("#preview");
          }, 500);
        }, function(error){
          scope.$parent.showAlert(error.data.response, "danger");
        });
      });
    }
  };

}]);














angular.module("Feedback").directive("confirm", ["$sce", function($sce) {
  "use strict";

  return {
    replace: true,
    templateUrl: "templates/confirm.html",
    scope: {
      title: "@",
      msg: "@message",
      okLabel: "@",
      alertType: "@",
      ok: "&"
    },
    link: function(scope, elem, attr){
      scope.message = $sce.trustAsHtml(scope.msg);
    }
  };

}]);















angular.module("Feedback").directive("login", ["AdminDao", function(AdminDao) {
  "use strict";

  return {
    replace: true,
    templateUrl: "templates/login.html",
    link: function(scope, elem, attr){
      scope.showError = function(msg){
        scope.haveError = true;
        scope.message = "And error ocurred! Please try again.";
        setTimeout(function(){
          scope.haveError = false;
          scope.$digest();
        }, 3000);
      };

      scope.login = function(){
        AdminDao.login(scope.username, scope.password, function(r){
          scope.$broadcast("login", r.token, r.username);
          $("#login").modal("hide");
        }, scope.showError);
      };
    }
  };

}]);
