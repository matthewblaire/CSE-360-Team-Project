package guiRequestQueue;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.Request;
import javafx.collections.FXCollections;

/*******
 * <p> Title: ControllerRequestQueue Class. </p>
 *
 * <p> Description: The Java/FX-based Request Queue Page.  This class provides the controller
 * actions based on the user's use of the JavaFX GUI widgets defined by the View class.
 *
 * Both admin and staff users can view this page.  Admin users additionally see:
 * <ul>
 *   <li>A severity ComboBox to reclassify the selected ticket.</li>
 *   <li>A close comment TextArea to record what was done when closing a ticket.</li>
 *   <li>Close Ticket and Reopen Ticket action buttons.</li>
 * </ul>
 *
 * A parallel {@link #currentRequests} list keeps {@link Request} objects index-aligned with
 * the ListView rows so a selected index maps directly to a database primary key.
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-04-09	Initial version
 * @version 1.01	2026-04-09	Added doSelectRequest, doCloseTicket, doReopenTicket
 * @version 1.02	2026-04-09	Added severity update and close comment
 */
public class ControllerRequestQueue {

	/** Formatter used when rendering timestamps in each list row and detail panel. */
	private static final DateTimeFormatter DISPLAY_FMT =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	/**
	 * Parallel list of {@link Request} objects aligned by index with the ListView rows.
	 * Allows doSelectRequest(), doCloseTicket(), doReopenTicket(), and doUpdateSeverity()
	 * to look up the selected Request without parsing the display string.
	 */
	static List<Request> currentRequests = new ArrayList<>();

	/** Default constructor — not used. */
	public ControllerRequestQueue() {
	}

	private static Database theDatabase = applicationMain.FoundationsMain.database;


	/**********
	 * <p> Method: doLoadRequests() </p>
	 *
	 * <p> Description: Fetches all requests (oldest-first), rebuilds the parallel list, and
	 * populates the ListView.  The detail panel and action controls are reset. </p>
	 */
	protected static void doLoadRequests() {
		List<Request> requests = theDatabase.getAllRequests();

		currentRequests.clear();
		currentRequests.addAll(requests);

		long openCount = requests.stream()
				.filter(r -> "OPEN".equals(r.getStatus()))
				.count();

		ViewRequestQueue.label_Summary.setText(
				"Open: " + openCount + "   |   Total: " + requests.size());

		List<String> rows = requests.stream()
				.map(ControllerRequestQueue::formatRequestRow)
				.toList();

		ViewRequestQueue.listview_Requests.getSelectionModel().clearSelection();
		ViewRequestQueue.listview_Requests.setItems(FXCollections.observableArrayList(rows));

		// Reset detail panel
		ViewRequestQueue.textarea_Detail.clear();
		ViewRequestQueue.textarea_CloseComment.clear();
		ViewRequestQueue.label_ActionFeedback.setText("");
		ViewRequestQueue.button_CloseTicket.setDisable(true);
		ViewRequestQueue.button_ReopenTicket.setDisable(true);
		ViewRequestQueue.button_UpdateSeverity.setDisable(true);
	}


	/**********
	 * <p> Method: doSelectRequest() </p>
	 *
	 * <p> Description: Fires when the selected ListView index changes.  Populates the detail
	 * TextArea, sets the severity ComboBox to the selected request's current severity, and
	 * enables the correct action button based on the ticket's status. </p>
	 */
	protected static void doSelectRequest() {
		int idx = ViewRequestQueue.listview_Requests.getSelectionModel().getSelectedIndex();
		if (idx < 0 || idx >= currentRequests.size()) {
			ViewRequestQueue.textarea_Detail.clear();
			ViewRequestQueue.button_CloseTicket.setDisable(true);
			ViewRequestQueue.button_ReopenTicket.setDisable(true);
			ViewRequestQueue.button_UpdateSeverity.setDisable(true);
			return;
		}

		Request r = currentRequests.get(idx);

		// Build the detail text
		String reopenLine = (r.getOriginalClosedRequestId() != null)
				? "\nReopened from: #" + r.getOriginalClosedRequestId()
				: "";
		StringBuilder detail = new StringBuilder(String.format(
				"ID: #%03d   |   Status: %s   |   Severity: %s   |   Submitted by: %s   |   %s%s%n%n"
				+ "Title: %s%n%nDescription:%n%s",
				r.getRequestId(),
				r.getStatus(),
				r.getSeverity(),
				r.getAuthorUsername(),
				r.getCreatedAt().format(DISPLAY_FMT),
				reopenLine,
				r.getTitle(),
				r.getDescription()));

		if (r.getCloseComment() != null && !r.getCloseComment().isBlank()) {
			detail.append("\n\nClose Comment:\n").append(r.getCloseComment());
		}

		ViewRequestQueue.textarea_Detail.setText(detail.toString());
		ViewRequestQueue.label_ActionFeedback.setText("");

		// Set the severity ComboBox to the current value
		ViewRequestQueue.combobox_Severity.getSelectionModel().select(r.getSeverity());
		ViewRequestQueue.button_UpdateSeverity.setDisable(false);

		// Enable the appropriate action button
		boolean isOpen = "OPEN".equals(r.getStatus());
		ViewRequestQueue.button_CloseTicket.setDisable(!isOpen);
		ViewRequestQueue.button_ReopenTicket.setDisable(isOpen);

		// Clear the close comment field so it's ready for a new comment
		if (isOpen) {
			ViewRequestQueue.textarea_CloseComment.clear();
		}
	}


	/**********
	 * <p> Method: doCloseTicket() </p>
	 *
	 * <p> Description: Closes the selected OPEN ticket, saving the text from the close comment
	 * TextArea as the resolution note.  The list is reloaded after a successful update. </p>
	 */
	protected static void doCloseTicket() {
		int idx = ViewRequestQueue.listview_Requests.getSelectionModel().getSelectedIndex();
		if (idx < 0 || idx >= currentRequests.size()) return;

		Request r = currentRequests.get(idx);
		if (!"OPEN".equals(r.getStatus())) return;

		String comment = ViewRequestQueue.textarea_CloseComment.getText();
		int updated = theDatabase.closeRequest(r.getRequestId(), comment);
		if (updated > 0) {
			doLoadRequests();
			ViewRequestQueue.label_ActionFeedback.setText(
					"Ticket #" + r.getRequestId() + " closed.");
			ViewRequestQueue.label_ActionFeedback.setStyle("-fx-text-fill: black;");
		} else {
			ViewRequestQueue.label_ActionFeedback.setText("Error: could not close ticket.");
			ViewRequestQueue.label_ActionFeedback.setStyle("-fx-text-fill: red;");
		}
	}


	/**********
	 * <p> Method: doReopenTicket() </p>
	 *
	 * <p> Description: Reopens the selected CLOSED ticket by setting its status back to OPEN
	 * and clearing its close comment.  The list is reloaded after a successful update. </p>
	 */
	protected static void doReopenTicket() {
		int idx = ViewRequestQueue.listview_Requests.getSelectionModel().getSelectedIndex();
		if (idx < 0 || idx >= currentRequests.size()) return;

		Request r = currentRequests.get(idx);
		if (!"CLOSED".equals(r.getStatus())) return;

		int updated = theDatabase.reopenRequest(r.getRequestId());
		if (updated > 0) {
			doLoadRequests();
			ViewRequestQueue.label_ActionFeedback.setText(
					"Ticket #" + r.getRequestId() + " reopened.");
			ViewRequestQueue.label_ActionFeedback.setStyle("-fx-text-fill: black;");
		} else {
			ViewRequestQueue.label_ActionFeedback.setText("Error: could not reopen ticket.");
			ViewRequestQueue.label_ActionFeedback.setStyle("-fx-text-fill: red;");
		}
	}


	/**********
	 * <p> Method: doUpdateSeverity() </p>
	 *
	 * <p> Description: Saves the severity level currently selected in the ComboBox for the
	 * selected ticket.  The list row is refreshed to immediately reflect the change. </p>
	 */
	protected static void doUpdateSeverity() {
		int idx = ViewRequestQueue.listview_Requests.getSelectionModel().getSelectedIndex();
		if (idx < 0 || idx >= currentRequests.size()) return;

		Request r = currentRequests.get(idx);
		String newSeverity = ViewRequestQueue.combobox_Severity.getValue();
		if (newSeverity == null) return;

		int updated = theDatabase.updateRequestSeverity(r.getRequestId(), newSeverity);
		if (updated > 0) {
			// Update the parallel list and the single ListView row in-place
			r.setSeverity(newSeverity);
			ViewRequestQueue.listview_Requests.getItems().set(idx, formatRequestRow(r));
			// Refresh the detail text so it reflects the new severity
			doSelectRequest();
			ViewRequestQueue.label_ActionFeedback.setText(
					"Severity updated to " + newSeverity + ".");
			ViewRequestQueue.label_ActionFeedback.setStyle("-fx-text-fill: black;");
		} else {
			ViewRequestQueue.label_ActionFeedback.setText("Error: could not update severity.");
			ViewRequestQueue.label_ActionFeedback.setStyle("-fx-text-fill: red;");
		}
	}


	/**
	 * Formats a single {@link Request} into the one-line string displayed in the ListView.
	 * Format: {@code [#003] [OPEN  ] [High    ] staff1 — "Title" — 2026-04-09 14:30}
	 *
	 * @param r the request to format
	 * @return the display string
	 */
	private static String formatRequestRow(Request r) {
		String statusPadded   = "OPEN".equals(r.getStatus()) ? "[OPEN  ]" : "[CLOSED]";
		String severity       = r.getSeverity() != null ? r.getSeverity() : "Medium";
		String severityPadded = String.format("[%-8s]", severity);
		String timestamp      = r.getCreatedAt().format(DISPLAY_FMT);
		String reopenedTag    = (r.getOriginalClosedRequestId() != null)
				? " [reopened from #" + r.getOriginalClosedRequestId() + "]"
				: "";
		return String.format("[#%03d] %s %s %s — \"%s\"%s — %s",
				r.getRequestId(),
				statusPadded,
				severityPadded,
				r.getAuthorUsername(),
				r.getTitle(),
				reopenedTag,
				timestamp);
	}


	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns admin users to Admin Home; all others to Staff Home. </p>
	 */
	protected static void performReturn() {
		if (ViewRequestQueue.theUser.getAdminRole()) {
			guiAdminHome.ViewAdminHome.displayAdminHome(
					ViewRequestQueue.theStage, ViewRequestQueue.theUser);
		} else {
			guiStaff.ViewStaffHome.displayStaffHome(
					ViewRequestQueue.theStage, ViewRequestQueue.theUser);
		}
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and returns to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRequestQueue.theStage);
	}


	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminates the application. </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
