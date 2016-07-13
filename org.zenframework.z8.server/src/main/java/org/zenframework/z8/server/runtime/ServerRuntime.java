package org.zenframework.z8.server.runtime;

import org.zenframework.z8.server.base.job.scheduler.Jobs;
import org.zenframework.z8.server.base.job.scheduler.TaskLogs;
import org.zenframework.z8.server.base.job.scheduler.Tasks;
import org.zenframework.z8.server.base.table.system.Domains;
import org.zenframework.z8.server.base.table.system.Entries;
import org.zenframework.z8.server.base.table.system.Files;
import org.zenframework.z8.server.base.table.system.MessagesQueue;
import org.zenframework.z8.server.base.table.system.Properties;
import org.zenframework.z8.server.base.table.system.Property;
import org.zenframework.z8.server.base.table.system.SecurityGroups;
import org.zenframework.z8.server.base.table.system.Sequences;
import org.zenframework.z8.server.base.table.system.SystemFiles;
import org.zenframework.z8.server.base.table.system.SystemTools;
import org.zenframework.z8.server.base.table.system.UserEntries;
import org.zenframework.z8.server.base.table.system.Users;
import org.zenframework.z8.server.ie.RmiTransportProcedure;
import org.zenframework.z8.server.ie.TransportRoutes;

public class ServerRuntime extends AbstractRuntime {

	public static final Property DbSchemeControlSumProperty = new Property("C0CDFF6D-9357-41FA-94B1-61D131CC0C09",
			"z8.database.schemeControlSum", "000.000.0000", "Контрольная сумма схемы базы данных");

	public static final Property PreserveMessagesQueueProperty = new Property("8D9C727A-34FC-4DCD-9AB0-5A2AF8E676E0",
			"z8.transport.preserveExportMessages", "false",
			"Сохранять локальную очередь экспортируемых сообщений (true / false)");
//	public static final Property EnableProtocolsProperty = new Property("222A95B9-05BC-4AF3-8425-323D8B1A1B73",
//			"z8.transport.enableProtocols", "", "Список (через ',') протоколов, инициализируемых при старте");
//	public static final Property ExportRecordsMaxProperty = new Property("FAB69475-114C-4C39-8401-A03C42608BBE",
//			"z8.transport.exportRecordsMax", "1000", "Максимальное количество записей в сообщении экспорта");
//	public static final Property RecordsSortingModeProperty = new Property("FA9D7410-050B-43BB-9F7A-B151F5BCD091",
//			"z8.transport.recordsSortingMode", "always",
//			"Режим сортировки записей при импорте/экспорте (on_export, on_import, always)");
//	public static final Property InactiveRouteTimeoutProperty = new Property("B7CA8C2A-430C-44BC-8DFA-FEF11A7B115B",
//			"z8.transport.inactiveRouteTimeout", "30",
//			"Время простоя маршрута, по которому не удалось отправить сообщение, минуты");
//	public static final Property FileFolderProperty = new Property("3D524DE7-0AF3-40FE-8FF7-C4A073D1F834",
//			"z8.transport.file.folder", "C:/z8/transport", "Каталог для обмена по протоколу file");
/*	public static final Property JmsConnectionFactoryProperty = new Property("5CACA325-B353-43C0-917C-B9A20C21C64E",
			"z8.transport.jms.connectionFactory", "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
			"Класс-фабрика для получения JMS-соединений");
	public static final Property JmsConnectionUrlProperty = new Property("7CAF19ED-71A5-480F-A2D3-95C5B22B4CA6",
			"z8.transport.jms.connectionUrl", "tcp://localhost:61616?trace=false&soTimeout=60000",
			"URL для подключения к JMS-серверу");
	public static final Property JmsModeProperty = new Property("543A3B9F-04F0-4697-AEBA-ABBF5693865B",
			"z8.transport.jms.mode", "object", "Режим передачи сообщений через JMS (object, stream). По умолчанию - object");
	public static final Property WsEndpointProperty = new Property("E90D5A9C-6C4A-48A2-BA3C-34E2F69DEF11",
			"z8.transport.ws.endpoint", "http://localhost:9898/transport", "URL транспортного web-сервиса");
*/
	public ServerRuntime() {
		addTable(new Users.CLASS<Users>(null));
		addTable(new SecurityGroups.CLASS<SecurityGroups>(null));
		addTable(new Sequences.CLASS<Sequences>(null));
		addTable(new Entries.CLASS<Entries>(null));

		addTable(new Domains.CLASS<Domains>(null));
		addTable(new UserEntries.CLASS<UserEntries>(null));

		addTable(new Jobs.CLASS<Jobs>(null));
		addTable(new Tasks.CLASS<Tasks>(null));
		addTable(new TaskLogs.CLASS<TaskLogs>(null));

		addTable(new Files.CLASS<Files>(null));
		addTable(new SystemFiles.CLASS<SystemFiles>(null));
		addTable(new Properties.CLASS<Properties>(null));

		addTable(new MessagesQueue.CLASS<MessagesQueue>(null));
		addTable(new TransportRoutes.CLASS<TransportRoutes>(null));

		addEntry(new SystemTools.CLASS<SystemTools>(null));

		addActivator(new Properties.PropertiesActivator.CLASS<Properties.PropertiesActivator>(null));

		addJob(new RmiTransportProcedure.CLASS<RmiTransportProcedure>(null));

		addProperty(DbSchemeControlSumProperty);
		addProperty(PreserveMessagesQueueProperty);
//		addProperty(EnableProtocolsProperty);
//		addProperty(ExportRecordsMaxProperty);
//		addProperty(RecordsSortingModeProperty);
//		addProperty(InactiveRouteTimeoutProperty);
//		addProperty(FileFolderProperty);
//		addProperty(JmsConnectionFactoryProperty);
//		addProperty(JmsConnectionUrlProperty);
//		addProperty(JmsModeProperty);
//		addProperty(WsEndpointProperty);
	}

}
