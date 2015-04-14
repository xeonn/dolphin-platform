var babelify = require('babelify');
var browserify = require('browserify');
var shim = require('browserify-shim');
var del = require('del');
var gulp = require('gulp');
var rename = require('gulp-rename');
var sourcemaps = require('gulp-sourcemaps');
var uglify = require('gulp-uglify');
var gutil = require('gulp-util');
var buffer = require('vinyl-buffer');
var source = require('vinyl-source-stream');

gulp.task('clean', function(cb) {
    del(['dist'], cb);
});

gulp.task('build', function() {

    browserify({
        entries: './src/dolphin.es6',
        standalone: 'dolphin',
        debug: true
    })
        //.external('./bower_components/opendolphin/index.js')
        .transform(babelify)
        .bundle()
        .on('error', gutil.log.bind(gutil, 'Browserify Error'))
        .pipe(source('dolphin.js'))
        .pipe(buffer())
        .pipe(rename({ extname: '.min.js' }))
        .pipe(sourcemaps.init({loadMaps: true}))
        //.pipe(uglify())
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest('./dist'))
});
