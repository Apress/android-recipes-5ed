#include <EGL/egl.h>
#include <GLES/gl.h>

#include <android/log.h>
#include <android/window.h>
#include <android_native_app_glue.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "AndroidRecipes", __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, "AndroidRecipes", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "AndroidRecipes", __VA_ARGS__))

//Data structure to hold that last known touch location
struct touch_state
{
    int32_t x;
    int32_t y;
};

//Data structure to hold the global state of the activity
struct driver
{
    struct android_app* app;
    struct touch_state state;

    EGLDisplay display;
    EGLSurface surface;
    EGLContext context;
    int32_t width;
    int32_t height;
};

/**
 * Helper function to render the next color frame in OpenGL
 */
static void render_frame(struct driver* driver)
{
    if (driver->display == NULL) {
        // No display.
        return;
    }

    float red = (float)driver->state.x / driver->width;
    float green = (float)driver->state.y / driver->height;
    float blue = 1 - (float)driver->state.x / driver->width;
    //Render the new color based on touch position
    glClearColor(red, green, blue, 1.0f);
    //Tell OpenGL to refresh the color buffer
    glClear(GL_COLOR_BUFFER_BIT);
    //Place the new frame onto the display buffer
    eglSwapBuffers(driver->display, driver->surface);
}

/**
 * Initialize an EGL context for the current display.
 */
static int engine_init_display(struct driver* driver) {
    // initialize OpenGL ES and EGL

    /*
     * Here specify the attributes of the desired configuration.
     * Below, we select an EGLConfig with at least 8 bits per color
     * component compatible with on-screen windows
     */
    const EGLint attribs[] = {
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_BLUE_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_RED_SIZE, 8,
            EGL_NONE
    };
    EGLint w, h, dummy, format;
    EGLint numConfigs;
    EGLConfig config;
    EGLSurface surface;
    EGLContext context;

    EGLDisplay display = eglGetDisplay(EGL_DEFAULT_DISPLAY);

    eglInitialize(display, 0, 0);

    /* Here, the application chooses the configuration it desires. In this
     * sample, we have a very simplified selection process, where we pick
     * the first EGLConfig that matches our criteria */
    eglChooseConfig(display, attribs, &config, 1, &numConfigs);

    /* EGL_NATIVE_VISUAL_ID is an attribute of the EGLConfig that is
     * guaranteed to be accepted by ANativeWindow_setBuffersGeometry().
     * As soon as we picked a EGLConfig, we can safely reconfigure the
     * ANativeWindow buffers to match, using EGL_NATIVE_VISUAL_ID. */
    eglGetConfigAttrib(display, config, EGL_NATIVE_VISUAL_ID, &format);

    ANativeWindow_setBuffersGeometry(driver->app->window, 0, 0, format);

    surface = eglCreateWindowSurface(display, config, driver->app->window, NULL);
    context = eglCreateContext(display, config, NULL, NULL);

    if (eglMakeCurrent(display, surface, surface, context) == EGL_FALSE) {
        LOGW("Unable to eglMakeCurrent");
        return -1;
    }

    eglQuerySurface(display, surface, EGL_WIDTH, &w);
    eglQuerySurface(display, surface, EGL_HEIGHT, &h);

    driver->display = display;
    driver->context = context;
    driver->surface = surface;
    driver->width = w;
    driver->height = h;

    // Initialize GL state.
    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);
    glEnable(GL_CULL_FACE);
    glShadeModel(GL_SMOOTH);
    glDisable(GL_DEPTH_TEST);

    return 0;
}

/**
 * Tear down the EGL context currently associated with the display.
 */
static void engine_term_display(struct driver* driver) {
    if (driver->display != EGL_NO_DISPLAY) {
        eglMakeCurrent(driver->display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        if (driver->context != EGL_NO_CONTEXT) {
            eglDestroyContext(driver->display, driver->context);
        }
        if (driver->surface != EGL_NO_SURFACE) {
            eglDestroySurface(driver->display, driver->surface);
        }
        eglTerminate(driver->display);
    }

    driver->display = EGL_NO_DISPLAY;
    driver->context = EGL_NO_CONTEXT;
    driver->surface = EGL_NO_SURFACE;
}

/*
 * This event handler will receive lifecycle events for
 * the enclosing Activity instance.
 */
static void handle_cmd(struct android_app* app, int32_t cmd)
{
    struct driver* driver = (struct driver*)app->userData;
    switch (cmd)
    {
        case APP_CMD_SAVE_STATE:
            LOGI("Save state");
            // The system has asked us to save our current state.  Do so.
            driver->app->savedState = malloc(sizeof(struct touch_state));
            *((struct touch_state*)driver->app->savedState) = driver->state;
            driver->app->savedStateSize = sizeof(struct touch_state);
            break;

        case APP_CMD_INIT_WINDOW:
            LOGI("Init window");
            // The window is being shown, get it ready.
            if (driver->app->window != NULL) {
                engine_init_display(driver);
                render_frame(driver);
            }
            break;

        case APP_CMD_TERM_WINDOW:
            LOGI("Terminate window");
            // The window is being hidden or closed, clean it up.
            engine_term_display(driver);
            break;

        case APP_CMD_PAUSE:
            LOGI("Pausing");
            break;

        case APP_CMD_RESUME:
            LOGI("Resuming");
            break;

        case APP_CMD_STOP:
            LOGI("Stopping");
            break;

        case APP_CMD_DESTROY:
            LOGI("Destroying");
            break;

        case APP_CMD_LOST_FOCUS:
            LOGI("Lost focus");
            break;

        case APP_CMD_GAINED_FOCUS:
            LOGI("Gained focus");
            break;
    }
}

/*
 * This event handler will be triggered to process input
 * events received by the polling loop in main.
 */
static int32_t handle_input(struct android_app* app, AInputEvent* event)
{
    struct driver* driver = (struct driver*)app->userData;
    //Save the latest touch event for use in rendering
    if (AInputEvent_getType(event) == AINPUT_EVENT_TYPE_MOTION)
    {
        driver->state.x = AMotionEvent_getX(event, 0);
        driver->state.y = AMotionEvent_getY(event, 0);
        return 1;
    }
    else if (AInputEvent_getType(event) == AINPUT_EVENT_TYPE_KEY)
    {
        LOGI("Received key event: %d", AKeyEvent_getKeyCode(event));
        if (AKeyEvent_getKeyCode(event) == AKEYCODE_BACK)
        {
            //Finish the Activity
            if (AKeyEvent_getAction(event) == AKEY_EVENT_ACTION_UP)
            {
                ANativeActivity_finish(app->activity);
            }
        }
        return 1;
    }
    return 0;
}

/*
 * This is the main entry point for the native code. This
 * code is called on a separate thread, created by the
 * native_app_glue APIs.
 */
void android_main(struct android_app* state)
{
    struct driver driver;

    app_dummy(); // prevent glue from being stripped


    memset(&driver, 0, sizeof(driver));
    //Hold a reference to our state driver in the app struct
    state->userData = &driver;
    //Define app event handlers
    state->onAppCmd = &handle_cmd;
    state->onInputEvent = &handle_input;

    driver.app = state;

    if (state->savedState != NULL) {
        // We are starting with a previous saved state; restore from it.
        driver.state = *(struct touch_state*)state->savedState;
    }

    while(1)
    {
        int ident;
        int fdesc;
        int events;
        struct android_poll_source* source;

        //Infinite loop to poll for incoming events in the message queue
        while ((ident = ALooper_pollAll(0, &fdesc, &events, (void**)&source)) >= 0)
        {
            //Each event will be processed in the handler function we attached
            if (source)
                source->process(state, source);

            //This will be set when the activity is being destroyed
            if (state->destroyRequested)
                return;
        }

        //On each loop, render the next frame...
        // OpenGL throttles this so the main loop will effectively
        // run at the framebuffer update rate (16.7ms in most cases)
        render_frame(&driver);
    }
}