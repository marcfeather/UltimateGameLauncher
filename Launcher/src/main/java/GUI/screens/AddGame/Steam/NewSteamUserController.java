package GUI.screens.AddGame.Steam;

import GUI.Menu;
import GUI.localization.Language;
import GUI.screens.Notification.*;
import api.GameLauncher.GameLauncher;
import api.GameLauncher.Steam.SteamConfigUser;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author BubbleEgg
 * @verions: 1.0.0
 **/

public class NewSteamUserController {
	
	@FXML
	private JFXTextField username;
	@FXML
	private JFXPasswordField password;
	@FXML
	private JFXTextField id;
	@FXML
	private Label error;
	@FXML
	private Label title;
	@FXML
	private Label usernameLabel;
	@FXML
	private Label passwordLabel;
	@FXML
	private JFXButton add;
	
	private Stage stage;
	private NewSteamUser newSteamUser;
	private GameLauncher launcher;
	
	public void init(Stage stage) {
		this.stage = stage;
		this.launcher = new GameLauncher();
		error.setVisible(false);
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				title.setText(Language.format(Menu.lang.getLanguage().WindowTitleAddSteamUser));
				usernameLabel.setText(Language.format(Menu.lang.getLanguage().Username));
				passwordLabel.setText(Language.format(Menu.lang.getLanguage().Password));
				add.setText(Language.format(Menu.lang.getLanguage().AddSteamUser));
			}
		});
	}
	
	public void setNewSteamUser(NewSteamUser newSteamUser) {
		this.newSteamUser = newSteamUser;
	}
	
	@FXML
	void onFinish(){
		if(username.getText().isEmpty()||password.getText().isEmpty()||id.getText().isEmpty()){
			error.setText(Language.format(Menu.lang.getLanguage().ErrorFillAllFields));
			error.setVisible(true);
			return;
		}
		if(launcher.steam.getUser(username.getText()).exists()){
			error.setText(Language.format(Menu.lang.getLanguage().ErrorUserAlreadyExists));
			error.setVisible(true);
			return;
		}
			launcher.getSteam().createUser(username.getText(), password.getText(), id.getText());
		if(launcher.getSteam().getUsernames().size()==1){
			launcher.getSteam().getUser(username.getText()).setAsMainAccount();
		}
		
		Notification note = new Notification();
		note.setText(Language.format(Menu.lang.getLanguage().ImportLibrary));
		note.setTitle("Steam");
		note.setIcon(NotificationIcon.QUESTION);
		note.addOption(Language.format(Menu.lang.getLanguage().ImportLibraryAll), ButtonAlignment.RIGHT, new ButtonCallback() {
			@Override
			public void onClick() {
				new Thread(new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						launcher.getSteam().getUser(username.getText()).addGames(true);
						return null;
					}
				}).start();
			}
		}, true);
		note.addOption(Language.format(Menu.lang.getLanguage().ImportLibraryOnlyPaid), ButtonAlignment.RIGHT, new ButtonCallback() {
			@Override
			public void onClick() {
				new Thread(new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						launcher.getSteam().getUser(username.getText()).addGames(false);
						return null;
					}
				}).start();
			}
		}, true);
		note.addOption(Language.format(Menu.lang.getLanguage().ImportLibraryNoThanks), ButtonAlignment.RIGHT, new ButtonCallback() {
			@Override
			public void onClick() {
			}
		}, true);
		note.show();
		
		stage.close();
		newSteamUser.onFinish();
	}
	
	@FXML
	void checkForID() {
		if(!username.getText().isEmpty()) {
			for(SteamConfigUser scu : launcher.getSteam().loadAccounts()){
				if(scu.getUsername().equalsIgnoreCase(username.getText())){
					this.id.setText(scu.getSteam64id());
				}
			}
		}
	}
	@FXML
	void onExit() {
		stage.close();
		newSteamUser.onCancel();
	}
	@FXML
	void onMinimize() {
		stage.setIconified(true);
	}
}
