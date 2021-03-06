package org.swrltab.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.swrlapi.core.SWRLAPIFactory;
import org.swrlapi.core.SWRLAPIOWLOntology;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.drools.core.DroolsFactory;
import org.swrlapi.drools.core.DroolsSWRLRuleEngineCreator;
import org.swrlapi.exceptions.SWRLAPIException;
import org.swrlapi.ui.dialog.SWRLAPIApplicationDialogManager;
import org.swrlapi.ui.model.SWRLAPIApplicationModel;
import org.swrlapi.ui.view.SWRLAPIApplicationView;
import org.swrlapi.ui.view.queries.SWRLAPIQueriesView;

/**
 * Standalone SWRLAPI-based application that presents a SQWRL editor and query execution graphical interface.
 * <p/>
 * The Drools rule engine is used for query execution.
 * <p/>
 * To invoke from Maven put <code>org.swrltab.ui.SQWRLTab</code> in the <code>mainClass</code> element of the
 * <code>exec-maven-plugin</code> plugin configuration in the Maven project POM and run with the <code>exec:java</code>
 * goal.
 *
 * @see org.swrlapi.ui.view.queries.SWRLAPIQueriesView
 */
public class SQWRLTab extends JFrame implements SWRLAPIApplicationView
{
	private static final long serialVersionUID = 1L;

	private static final String APPLICATION_NAME = "SQWRLTab";
	private static final int APPLICATION_WINDOW_WIDTH = 1000;
	private static final int APPLICATION_WINDOW_HEIGHT = 580;

	private final SWRLAPIQueriesView queriesView;

	public static void main(String[] args)
	{
		// TODO Hard code temporarily for testing. SWRLCoreTests, SQWRLCollectionsTests, SQWRLCoreTests, SWRLInferenceTests
		String owlFileName = SQWRLTab.class.getClassLoader().getResource("projects/SQWRLCollectionsTests.owl").getFile();
		File owlFile = new File(owlFileName);

		try {
			// Create a SWRLAPI OWL ontology from the OWL ontology in the supplied file
			SWRLAPIOWLOntology swrlapiOWLOntology = SWRLAPIFactory.createOntology(owlFile);

			// Create a Drools-based query engine
			SWRLRuleEngine queryEngine = SWRLAPIFactory.createSWRLRuleEngine(swrlapiOWLOntology,
					new DroolsSWRLRuleEngineCreator());

			// Create the application model, supplying it with the ontology and query engine
			SWRLAPIApplicationModel applicationModel = SWRLAPIFactory.createApplicationModel(swrlapiOWLOntology, queryEngine);

			// Create the application controller
			SWRLAPIApplicationDialogManager applicationDialogManager = SWRLAPIFactory
					.createApplicationDialogManager(applicationModel);

			// Create the application view
			SQWRLTab applicationView = new SQWRLTab(applicationModel, applicationDialogManager);

			// Make the view visible
			applicationView.setVisible(true);

		} catch (RuntimeException e) {
			System.err.println("Error starting application: " + e.getMessage());
			System.exit(-1);
		}
	}

	public SQWRLTab(SWRLAPIApplicationModel applicationModel, SWRLAPIApplicationDialogManager applicationDialogManager)
			throws SWRLAPIException
	{
		super(APPLICATION_NAME);
		this.queriesView = createAndAddSWRLAPIQueriesView(applicationModel, applicationDialogManager);
	}

	@Override
	public String getApplicationName()
	{
		return APPLICATION_NAME;
	}

	@Override
	public void update()
	{
		this.queriesView.update();
	}

	private SWRLAPIQueriesView createAndAddSWRLAPIQueriesView(SWRLAPIApplicationModel applicationModel,
			SWRLAPIApplicationDialogManager applicationDialogManager) throws SWRLAPIException
	{
		Icon ruleEngineIcon = DroolsFactory.getSWRLRuleEngineIcon();
		SWRLAPIQueriesView queriesView = new SWRLAPIQueriesView(applicationModel, applicationDialogManager, ruleEngineIcon);
		Container contentPane = getContentPane();

		contentPane.setLayout(new BorderLayout());
		contentPane.add(queriesView);
		setSize(APPLICATION_WINDOW_WIDTH, APPLICATION_WINDOW_HEIGHT);

		return queriesView;
	}

	@Override
	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			this.setVisible(false);
			System.exit(0);
		}
	}

	@SuppressWarnings("unused")
	private static void Usage()
	{
		System.err.println("Usage: " + SQWRLTab.class.getName() + " <owlFileName>");
		System.exit(1);
	}
}
