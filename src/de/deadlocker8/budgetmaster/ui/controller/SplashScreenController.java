package de.deadlocker8.budgetmaster.ui.controller;

import java.io.IOException;

import de.deadlocker8.budgetmaster.logic.Settings;
import de.deadlocker8.budgetmaster.logic.utils.Colors;
import de.deadlocker8.budgetmaster.logic.utils.FileHelper;
import de.deadlocker8.budgetmaster.logic.utils.Helpers;
import de.deadlocker8.budgetmaster.logic.utils.Strings;
import fontAwesome.FontIconType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logger.Logger;
import tools.AlertGenerator;
import tools.ConvertTo;
import tools.HashUtils;
import tools.Localization;

public class SplashScreenController
{
	@FXML private ImageView imageViewLogo;
	@FXML private Label labelVersion;
	@FXML private PasswordField textFieldPassword;
	@FXML private Button buttonLogin;	

	private Stage stage;
	private Image icon;
	private Settings settings;
	private boolean isFirstStart;

	public void init(Stage stage, Image icon)
	{
		this.stage = stage;
		this.icon = icon;
		
		imageViewLogo.setImage(icon);
		
		labelVersion.setText("v" + Localization.getString(Strings.VERSION_NAME));
	
		buttonLogin.setGraphic(Helpers.getFontIcon(FontIconType.SIGN_IN, 18, Color.WHITE));
		buttonLogin.setStyle("-fx-background-color: " + ConvertTo.toRGBHexWithoutOpacity(Colors.BACKGROUND_BUTTON_BLUE) + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
		buttonLogin.setPadding(new Insets(3, 7, 3, 7));		
		
		textFieldPassword.setOnKeyReleased((event)->{
			if(event.getCode() == KeyCode.ENTER)
			{
				login();
			}
		});
		
		settings = FileHelper.loadSettings();		
		
		if(settings == null)
		{	
			settings = new Settings();
			//first start of budgetmaster
			Platform.runLater(() -> {
				AlertGenerator.showAlert(AlertType.INFORMATION, 
										Localization.getString(Strings.INFO_TITLE_WELCOME), 
										Localization.getString(Strings.INFO_HEADER_TEXT_WELCOME),
										Localization.getString(Strings.INFO_TEXT_WELCOME_FIRST_START),
										icon, 
										stage, 
										null, 
										false);
			});
			isFirstStart = true;
		}
		else
		{
			if(settings.getClientSecret() == null)
			{
				//compatibility (settings exists but from older version without clientSecret)
				Platform.runLater(() -> {
					AlertGenerator.showAlert(AlertType.INFORMATION,
											Localization.getString(Strings.INFO_TITLE_WELCOME), 
											Localization.getString(Strings.INFO_HEADER_TEXT_WELCOME),
											Localization.getString(Strings.INFO_TEXT_WELCOME_COMPATIBILITY),
											icon,
											stage,
											null,
											false);
				});
				isFirstStart = true;
			}
			else
			{
				isFirstStart = false;
			}
		}
	}
	
	public void login()
	{
		String password = textFieldPassword.getText().trim();
		if(password == null || password.isEmpty())
		{
			AlertGenerator.showAlert(AlertType.WARNING, 
									Localization.getString(Strings.TITLE_WARNING), 
									"", 
									Localization.getString(Strings.WARNING_EMPTY_PASSWORD), 
									icon, 
									stage, 
									null, 
									false);
			return;
		}		
	
		if(isFirstStart)
		{
			//save to settings
			settings.setClientSecret(HashUtils.hash(password, Helpers.SALT));
			try
			{
				FileHelper.saveSettings(settings);
				
				stage.close();
				openBudgetMaster();	
			}
			catch(IOException e)
			{
				Logger.error(e);
				AlertGenerator.showAlert(AlertType.ERROR, 
										Localization.getString(Strings.TITLE_ERROR), 
										"", 
										Localization.getString(Strings.ERROR_PASSWORD_SAVE),
										icon, 
										stage, 
										null, 
										false);
			}
		}
		else
		{			
			//check password
			if(!HashUtils.hash(password, Helpers.SALT).equals(settings.getClientSecret()))
			{
				AlertGenerator.showAlert(AlertType.WARNING, 
										Localization.getString(Strings.TITLE_WARNING), 
										"", 
										Localization.getString(Strings.WARNING_WRONG_PASSWORD),
										icon, 
										stage, 
										null, 
										false);
				return;
			}
			
			stage.close();
			openBudgetMaster();	
		}
	}
	
	private void openBudgetMaster()
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/deadlocker8/budgetmaster/ui/fxml/GUI.fxml"));
			fxmlLoader.setResources(Localization.getBundle());
			Parent root = (Parent)fxmlLoader.load();
			Stage newStage = new Stage();
			newStage.setTitle(Localization.getString(Strings.APP_NAME));
			newStage.setScene(new Scene(root, 650, 675));
			newStage.getIcons().add(icon);			
			newStage.setResizable(true);
			newStage.setMinHeight(650);
			newStage.setMinWidth(610);
			newStage.getScene().getStylesheets().add("/de/deadlocker8/budgetmaster/ui/style.css");
			Controller newController = fxmlLoader.getController();
			newController.init(newStage, settings);
			newStage.show();
		}
		catch(IOException e)
		{
			Logger.error(e);
		}
	}
}