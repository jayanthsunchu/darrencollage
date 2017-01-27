package src.client.dcollage.darrencollage.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import src.client.dcollage.darrencollage.DialogCloseListener;
import src.client.dcollage.darrencollage.R;
import src.client.dcollage.darrencollage.helpers.Constant;

/**
 * Created by wormhol3 on 1/15/2017.
 */

public class OptionsFragment extends AppCompatDialogFragment {
    public OptionsFragment() {}
    @Override
    public void onDismiss(DialogInterface dialog) {
        if(getActivity() instanceof DialogCloseListener){
            ((DialogCloseListener) getActivity()).SetLayout();
        }
        super.onDismiss(dialog);
    }
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AppCompatDialog(getActivity(), R.style.DialogThemeTwo);
    }

    private void OneClick(){
        editor.putInt("layoutid", Constant.LAYOUT_ONE);
        editor.commit();
        Toast.makeText(getContext(), "1st Layout Selected.", Toast.LENGTH_LONG).show();
    }
    private void TwoClick() {
        editor.putInt("layoutid", Constant.LAYOUT_TWO);
        editor.commit();
        Toast.makeText(getContext(), "2nd Layout Selected.", Toast.LENGTH_LONG).show();
    }

    private void SetLayout() {
        editor.putString("layouttitle", Title.getText().toString());
        editor.commit();
        editor.putString("layoutsize", Size.getText().toString());
        editor.commit();
        this.dismiss();
    }

    View view;
    @Override
    public void setupDialog(Dialog dialog, int style){
        AppCompatDialog acd = (AppCompatDialog) dialog;
        acd.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        acd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        acd.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    View One;
    View Two;
    Button SetLayout;
    AutoCompleteTextView Title;
    AutoCompleteTextView Size;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.options_layout, container);
        One = view.findViewById(R.id.layout_one);
        Two = view.findViewById(R.id.layout_two);
        SetLayout = (Button)view.findViewById(R.id.btn_set_layout);
        Title = (AutoCompleteTextView)view.findViewById(R.id.edit_title);
        Size = (AutoCompleteTextView)view.findViewById(R.id.edit_size);
        ArrayAdapter<String> adapterT = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, Constant.ITEM_TITLES);
        ArrayAdapter<String> adapterS = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, Constant.ITEM_SIZE);
        Title.setAdapter(adapterT);
        Size.setAdapter(adapterS);
        settings = getContext().getSharedPreferences(Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        editor = settings.edit();

        One.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OneClick();
            }
        });
        Two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwoClick();
            }
        });
        SetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetLayout();
            }
        });
        return view;
    }
}
