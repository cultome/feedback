module.exports = function(grunt) {
  grunt.initConfig({
    pkg: "Feedback",

    // "Compilamos" el codigo
    jshint: {
      src: ["js/app.js", "js/admin/app.js"],
      options: {
        "node": true,
        "esnext": true,
        "bitwise": false,
        "curly": false,
        "eqeqeq": true,
        "eqnull": true,
        "immed": true,
        "latedef": true,
        "newcap": true,
        "noarg": true,
        "undef": true,
        "strict": true,
        "trailing": true,
        "smarttabs": true,
        globals: {
          "angular": true,
          "$": true,
          "_": true,
          "CodeMirror": true,
          "document": true,
          "serviceEndpoint": true,
          "Chart": true
        }
      }
    },

    // Limpiamos el folder build
    clean: {
      build: {
        src: "build"
      },
      dist: {
        src: ["../src/main/webapp/fonts", "../src/main/webapp/img", "../src/main/webapp/admin.html", "../src/main/webapp/admin.min.js", "../src/main/webapp/app.min.js", "../src/main/webapp/feedback.min.css", "../src/main/webapp/index.html"]
      }
    },

    // convertimos los templates a javascript
    html2js: {
      options: {
        base: "templates",
        module: "templates",
        singleModule: true,
        htmlmin: {
          collapseBooleanAttributes: false,
          collapseWhitespace: true,
          removeAttributeQuotes: true,
          removeComments: true,
          removeEmptyAttributes: true,
          removeRedundantAttributes: true,
          removeScriptTypeAttributes: true,
          removeStyleLinkTypeAttributes: true
        }
      },
      templates: {
        src: "templates/**/*.html",
        dest: "build/templates.js"
      },
    },

    // Minimizamos los templates y el codigo
    uglify: {
      options: {
        banner: "/*! Feedback <%= grunt.template.today('yyyy-mm-dd') %> */\n"
      },
      app: {
        files: {
          "build/app.min.js": "js/app.js",
          "build/admin_app.min.js": "build/tmp.js",
          "build/templates.min.js": "build/templates.js",
          "build/active-line.min.js": "js/active-line.js",
          "build/codemirror.min.js": "js/codemirror.js",
          "build/matchbrackets.min.js": "js/matchbrackets.js",
          "build/show-hint.min.js": "js/show-hint.js",
          "build/simple.min.js": "js/simple.js",
          "build/Chart.Core.min.js": "js/Chart.Core.js",
          "build/Chart.Bar.min.js": "js/Chart.Bar.js",
          "build/Chart.Doughnut.min.js": "js/Chart.Doughnut.js",
          "build/Chart.Line.min.js": "js/Chart.Line.js",
          "build/Chart.PolarArea.min.js": "js/Chart.PolarArea.js",
          "build/Chart.Radar.min.js": "js/Chart.Radar.js"
        }
      }
    },

    cssmin: {
      minify: {
        files: {
          "build/app.min.css": "css/app.css",
          "build/codemirror.min.css": "css/codemirror.css",
          "build/show-hint.min.css": "css/show-hint.css"
        }
      }
    },

    copy: {
      substitutePathsJS: {
        src: "js/admin/app.js",
        dest: "build/tmp.js",
        options: {
          process: function(content, srcpath){
            return content.replace(/\/\*,"templates"\*\//, ",\"templates\"").replace(/templates\//g, "");
          }
        }
      },

      substitutePathsCSS: {
        src: "../src/main/webapp/feedback.min.css",
        dest: "../src/main/webapp/feedback.min.css",
        options: {
          process: function(content, srcpath){
            return content.replace(/\.\.\/fonts\//g, "fonts/");
          }
        }
      },

      replaceImportsInIndex: {
        files: [
          {
            expand: true,
            src: ["index.html", "admin.html"],
            dest: "build/",
          }
        ],

        options: {
          process: function(content, srcpath){
            grunt.log.writeln("Modificando los imports de js en index.html");
            var retVal = content;
            // eliminamos los imports de nuestros js
            retVal = retVal.replace(/.*?<script type="text\/javascript" src="js\/(.*?)"><\/script>/g, "");
            retVal = retVal.replace(/.*?<link rel="stylesheet" type="text\/css" href="css\/(.*?)" \/>.*?/g, "");

            retVal = retVal.replace(/.*?<!-- admin.js -->.*?/g, '<script type="text/javascript" src="admin.min.js"></script>');
            retVal = retVal.replace(/.*?<!-- app.js -->.*?/g, '<script type="text/javascript" src="app.min.js"></script>');
            retVal = retVal.replace(/.*?<!-- app.css -->.*?/g, '<link rel="stylesheet" type="text/css" href="feedback.min.css" />');
            
            return retVal;
          }
        }
      },

      dist: {
        expand: true,
        src: ["img/*", "fonts/*"],
        dest: "../src/main/webapp/"
      }

    },

    // minimizamos el indice
    htmlmin: {
      index: {
        options: {
          collapseBooleanAttributes: true,
          collapseWhitespace: true,
          removeAttributeQuotes: true,
          removeComments: true,
          removeEmptyAttributes: true,
          removeRedundantAttributes: true,
          removeScriptTypeAttributes: true,
          removeStyleLinkTypeAttributes: true
        },
        files: {
          "../src/main/webapp/index.html": "build/index.html",
          "../src/main/webapp/admin.html": "build/admin.html"
        }
      }
    },

    // Concatenamos los archivos
    concat: {
      options: {
        stripBanners: true
      },
      admin: {
        src: [
          "js/jquery.min.js",
          "js/bootstrap.min.js",
          "js/underscore-min.js",
          "js/angular.min.js",
          "js/angular-route.min.js",
          "js/angular-resource.min.js",
          "js/angular-sanitize.min.js",
          "build/codemirror.min.js",
          "build/active-line.min.js",
          "build/matchbrackets.min.js",
          "build/show-hint.min.js",
          "build/simple.min.js",
          "build/admin_app.min.js",
          "build/templates.min.js",
          "build/Chart.Core.min.js",
          "build/Chart.Bar.min.js",
          "build/Chart.Doughnut.min.js",
          "build/Chart.Line.min.js",
          "build/Chart.PolarArea.min.js",
          "build/Chart.Radar.min.js"
        ],
        dest: "../src/main/webapp/admin.min.js",
      },
      app: {
        src: [
          "js/jquery.min.js",
          "js/bootstrap.min.js",
          "js/underscore-min.js",
          "js/angular.min.js",
          "js/angular-route.min.js",
          "js/angular-resource.min.js",
          "js/angular-sanitize.min.js",
          "build/app.min.js"
        ],
        dest: "../src/main/webapp/app.min.js",
      },
      cssmin: {
        src: [
          "css/bootstrap-theme.min.css",
          "css/bootstrap.min.css",
          "build/codemirror.min.css",
          "build/show-hint.min.css",
          "build/app.min.css"
        ],
        dest: "../src/main/webapp/feedback.min.css",
      }
    }

  });

  grunt.loadNpmTasks("grunt-contrib-jshint");
  grunt.loadNpmTasks("grunt-contrib-concat");
  grunt.loadNpmTasks("grunt-contrib-uglify");
  grunt.loadNpmTasks("grunt-contrib-copy");
  grunt.loadNpmTasks("grunt-contrib-cssmin");
  grunt.loadNpmTasks("grunt-contrib-clean");
  grunt.loadNpmTasks("grunt-html2js");
  grunt.loadNpmTasks("grunt-contrib-htmlmin");

  // Tasks
  grunt.registerTask("default", ["jshint:src", "clean:build", "clean:dist", "html2js:templates", "copy:substitutePathsJS", "copy:replaceImportsInIndex", "uglify:app", "concat:admin", "concat:app", "cssmin", "concat:cssmin", "htmlmin:index", "copy:substitutePathsCSS", "copy:dist"]);
};