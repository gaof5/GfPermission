package com.gaof.premission;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.gaof.premission.source.ContextSource;
import com.gaof.premission.source.FragmentSource;
import com.gaof.premission.source.SupportFragmentSource;

public class GfPermission {

    /**
     * With {@link Fragment}.
     *
     * @param fragment {@link Fragment}.
     * @return {@link PermissionManagerEasy}.
     */
    public static PermissionManagerEasy with(Fragment fragment) {
        return new PermissionManagerEasy(new SupportFragmentSource(fragment));
    }

    /**
     * With {@link android.app.Fragment}.
     *
     * @param fragment {@link android.app.Fragment}.
     * @return {@link PermissionManagerEasy}.
     */
    public static PermissionManagerEasy with(android.app.Fragment fragment) {
        return new PermissionManagerEasy(new FragmentSource(fragment));
    }

    /**
     * With context.
     *
     * @param context {@link Context}.
     * @return {@link PermissionManagerEasy}.
     */
    public static PermissionManagerEasy with(Context context) {
        return new PermissionManagerEasy(new ContextSource(context));
    }

}
