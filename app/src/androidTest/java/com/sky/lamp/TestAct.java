package com.sky.lamp;

import android.test.ActivityInstrumentationTestCase2;

public class TestAct extends ActivityInstrumentationTestCase2 {
    public TestAct(String pkg, Class activityClass) {
        super(pkg, activityClass);
    }

    public TestAct(Class activityClass) {
        super(activityClass);
    }

    public void testStart() {
        getActivity();
    }


}
