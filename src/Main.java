import services.ProjectService;
import services.ReportService;
import services.TaskService;
import utils.ConsoleMenu;
import utils.Seed;
import utils.SessionManager;


void main() {
    final ProjectService projectService = new ProjectService(Seed.seedProjects());
    final TaskService taskService = new TaskService(Seed.seedTasks(), projectService);
    final ReportService reportService = new ReportService(projectService, taskService);

    final SessionManager sessionManager = new SessionManager();

    final ConsoleMenu menu;
    final Scanner scanner;

    scanner = new Scanner(System.in);
    menu = new ConsoleMenu(projectService, taskService, scanner);
    ConsoleApp app = new ConsoleApp(menu, scanner, projectService, taskService, reportService, sessionManager);
    app.runApplication();


    scanner.close();
    menu.displayExitMessage();
}
