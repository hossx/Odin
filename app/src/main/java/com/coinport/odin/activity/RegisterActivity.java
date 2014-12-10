package com.coinport.odin.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.coinport.odin.R;

public class RegisterActivity extends Activity {
    private final String termsUri = "https://exchange.coinport.com/terms.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_register);

        TextView terms = (TextView) findViewById(R.id.register_terms);
        PackageManager pm = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(termsUri));
        if (testIntent.resolveActivity(pm) != null) {
            terms.setText(Html.fromHtml("<a href=\"" + termsUri + "\">" + getString(R.string.register_terms) + "</a>"));
            terms.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            terms.setText(getString(R.string.register_terms) + "(" + termsUri + ")");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
