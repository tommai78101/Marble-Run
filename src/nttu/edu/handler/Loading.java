package nttu.edu.handler;

import nttu.edu.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class Loading extends Dialog {
	
	public Loading(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		this.setContentView(R.layout.loading_dialog);
	}
}
