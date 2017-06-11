package de.deadlocker8.budgetmaster.main;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import de.deadlocker8.budgetmaster.ui.SplashScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logger.FileOutputMode;
import logger.Logger;
import tools.PathUtils;

public class Main extends Application
{
	public static ResourceBundle bundle = ResourceBundle.getBundle("de/deadlocker8/budgetmaster/main/", Locale.GERMANY);

	@Override
	public void start(Stage stage)
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("de/deadlocker8/budgetmaster/ui/SplashScreen.fxml"));
			Parent root = (Parent)loader.load();
			
			Scene scene = new Scene(root, 400, 230);

			((SplashScreenController)loader.getController()).init(stage, new Image("/de/deadlocker8/budgetmaster/resources/icon.png"), bundle);

			stage.setResizable(false);			
			stage.getIcons().add(new Image("/de/deadlocker8/budgetmaster/resources/icon.png"));
			stage.setTitle(bundle.getString("app.name"));
			stage.setScene(scene);			
			stage.show();
		}
		catch(Exception e)
		{
			Logger.error(e);
		}
	}

	@Override
	public void init() throws Exception
	{
		Parameters params = getParameters();
		String logLevelParam = params.getNamed().get("loglevel");
		Logger.setLevel(logLevelParam);
		
		File logFolder = new File(PathUtils.getOSindependentPath() + bundle.getString("folder"));
		PathUtils.checkFolder(logFolder);
		Logger.enableFileOutput(logFolder, System.out, System.err, FileOutputMode.COMBINED);

		Logger.appInfo(bundle.getString("app.name"), bundle.getString("version.name"), bundle.getString("version.code"), bundle.getString("version.date"));
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}