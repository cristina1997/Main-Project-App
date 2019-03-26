package sw.gmit.ie.crist.cameradetection.Activities;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class NavMenu {
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private TextView headerName;

    public TextView getHeaderName() {
        return headerName;
    }

    public void setHeaderName(TextView userName) {
        this.headerName = userName;
    }

 

    public DrawerLayout getDrawer() {
        return drawer;
    }

    public void setDrawer(DrawerLayout drawer) {
        this.drawer = drawer;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public void setNavigationView(NavigationView navigationView) {
        this.navigationView = navigationView;
    }

    public ActionBarDrawerToggle getToggle() {
        return toggle;
    }

    public void setToggle(ActionBarDrawerToggle toggle) {
        this.toggle = toggle;
    }
}
