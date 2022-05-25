public class Main {

	public static void main(String[] args)
	{

		// TODO  !! LOCATIE BAZA DE DATE AICI !!
		DB db = new DB("");

				try {
					MainMenu frame = new MainMenu(db);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}

	}

}
