package com.kung.dppf.widget;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kung.dppf.R;

public class ConformDialog extends Dialog {
	public ConformDialog(Context context) {  
        super(context);  
    }  
  
    public ConformDialog(Context context, int theme) {  
        super(context, theme);  
    }  
  
    public static class Builder {  
        private Context context;  
        private String title;  
        private String message1;
        private String message2;
        private boolean is_msg_color = false;
        private String positiveButtonText;  
        private String negativeButtonText;  
        private View contentView;  
        private EditText view; 
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
  
        public Builder(Context context) {  
            this.context = context;  
        }  
  
        public Builder setMessage(String message1,String message2) {
            this.message1 = message1;
            this.message2 = message2;
            return this;  
        }  
  
        /** 
         * Set the Dialog message from resource 
         *  
         * @param message
         * @return 
         */  
        public Builder setMessage(int message) {  
            this.message1 = (String) context.getText(message);
            return this;  
        }

        public Builder setMessage(String message,boolean bln_color) {
            this.message1 = message;
            this.is_msg_color = bln_color;
            return this;
        }
  
        /** 
         * Set the Dialog title from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setTitle(int title) {  
            this.title = (String) context.getText(title);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from String 
         *  
         * @param title 
         * @return 
         */  
  
        public Builder setTitle(String title) {  
            this.title = title;  
            return this;  
        }  
  
        public Builder setContentView(View v) {  
            this.contentView = v;  
            return this;  
        } 
        
        public Builder setView(EditText v){
        	this.view = v;  
            return this; 
        }
        
        public EditText getView(){
            return view; 
        }
  
        /** 
         * Set the positive button resource and it's listener 
         *  
         * @param positiveButtonText 
         * @return 
         */  
        public Builder setPositiveButton(int positiveButtonText,  
                OnClickListener listener) {
            this.positiveButtonText = (String) context  
                    .getText(positiveButtonText);  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setPositiveButton(String positiveButtonText,  
                OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(int negativeButtonText,  
                OnClickListener listener) {
            this.negativeButtonText = (String) context  
                    .getText(negativeButtonText);  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(String negativeButtonText,  
                OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public ConformDialog create() {  
            LayoutInflater inflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final ConformDialog dialog = new ConformDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_conform_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            dialog.setCanceledOnTouchOutside(false);
            // set the dialog title  
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button  
            if (positiveButtonText != null) {  
                ((TextView) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);  
                if (positiveButtonClickListener != null) {  
                    ((TextView) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    positiveButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_POSITIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);  
            }  
            // set the cancel button  
            if (negativeButtonText != null) {  
                ((TextView) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);  
                if (negativeButtonClickListener != null) {  
                    ((TextView) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    negativeButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_NEGATIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);  
            }  
            // set the content message  
            if (message1 != null) {
//                ((TextView) layout.findViewById(R.id.message)).setText(message);
                if(is_msg_color){
                    ((TextView) layout.findViewById(R.id.message1)).setText(Html.fromHtml(message1));
                    ((TextView) layout.findViewById(R.id.message2)).setText(Html.fromHtml(message2));
                }else{
                    ((TextView) layout.findViewById(R.id.message1)).setText(message1);
                    ((TextView) layout.findViewById(R.id.message2)).setText(message2);
                }
            } else if (contentView != null) {  
                // if no message set  
                // add the contentView to the dialog body  
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();  
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));  
            } 
            
            dialog.setContentView(layout);  
            return dialog;  
        }
    }  
}
