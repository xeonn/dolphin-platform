var gulp = require('gulp');
var del = require('del');
var gutil = require('gulp-util');
var browserify = require('browserify');
var watchify = require('watchify');
var buffer = require('vinyl-buffer');
var source = require('vinyl-source-stream');
var sourcemaps = require('gulp-sourcemaps');
var babelify = require('babelify');

gulp.task('clean', function(cb) {
    del(['js'], cb);
});

gulp.task('scripts', function() {

    var bundler = watchify(browserify({
        entries: ['./src/app.es6'],
        transform: [babelify],
        extensions: ['.es6'],
        insertGlobals: true,
        cache: {},
        packageCache: {},
        ignoreMissing: true,
        fullPaths: true,
        debug: true
    }));

    bundler.on('update', rebundle);

    function rebundle() {
        return bundler.bundle()
            .on('error', gutil.log.bind(gutil, 'Browserify Error'))
            .pipe(source('bundle.js'))
            .pipe(buffer())
            .pipe(sourcemaps.init({loadMaps: true}))
            .pipe(sourcemaps.write('./'))
            .pipe(gulp.dest('./js'))
    }

    return rebundle();
});
