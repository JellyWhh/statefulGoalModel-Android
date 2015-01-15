/**
 * 
 */
package edu.fudan.se.goalmodel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.fudan.se.goalmachine.Condition;
import edu.fudan.se.goalmachine.ElementMachine;
import edu.fudan.se.goalmachine.GoalMachine;
import edu.fudan.se.goalmachine.TaskMachine;

/**
 * @author whh
 * 
 */
public class GmXMLParser {

	public static GoalModel newGoalModel(String filename) {
		GoalModel goalModel = new GoalModel();
		try {
			// 得到DOM解析器的工厂实例
			DocumentBuilderFactory domfac = DocumentBuilderFactory
					.newInstance();
			// 从DOM工厂获得DOM解析器
			DocumentBuilder dombuilder = domfac.newDocumentBuilder();
			// 把要解析的XML文档转化为输入流，以便DOM解析器解析它
			InputStream is = new FileInputStream(filename);
			// 解析XML文档的输入流，得到一个Document
			Document doc = dombuilder.parse(is);
			// 得到XML文档的根节点(books)
			Element root = doc.getDocumentElement();
			// 获得根节点的所有属性名和值
			if (root.getAttributes().getLength() > 0) {
				if (root.getAttributeNode("name") != null) {
					goalModel.setName(root.getAttributeNode("name").getValue());
				}
				if (root.getAttributeNode("description") != null) {
					goalModel.setDescription(root.getAttributeNode(
							"description").getValue());
				}
			}

			// 得到根节点的所有子节点，也就是ElementMachine节点
			NodeList emNodeList = root.getChildNodes();
			for (int i = 0; i < emNodeList.getLength(); i++) {

				Node emNode = emNodeList.item(i);// 这个node是element machine节点
				// 判断是不是子节点
				if (emNode.getNodeType() == Node.ELEMENT_NODE) {
					// ElementMachine elementMachine;
					int id, level = 0;
					String type = "", name = "", description = "";
					// 获得子节点的属性的名和值，也就是id, type, name,level等属性
					if (emNode.getAttributes().getLength() > 0) {
						for (int j = 0; j < emNode.getAttributes().getLength(); j++) {
							switch (emNode.getAttributes().item(j)
									.getNodeName()) {
							case "id":
								id = Integer.parseInt(emNode.getAttributes()
										.item(j).getNodeValue());
								break;
							case "type":
								type = emNode.getAttributes().item(j)
										.getNodeValue();
								break;

							case "name":
								name = emNode.getAttributes().item(j)
										.getNodeValue();
								break;

							case "level":
								level = Integer.parseInt(emNode.getAttributes()
										.item(j).getNodeValue());
								break;
							case "description":
								description = emNode.getAttributes().item(j)
										.getNodeValue();
								break;

							default:
								break;
							}
						}
					}

					// 获得子节点的所有子节点，也就是decomposition, schedulerMethod,
					// condition等节点
					NodeList propertyNodeList = emNode.getChildNodes();
					// System.err.println(propertyNodeList.getLength());
					String parentGoal = "";
					int decomposition = 0, schedulerMethod = 0, priorityLevel = 0, waitingTimeLimit = 0;
					boolean needDelegate = false, needPeopleInteraction = false;

					String executingRequestedServiceName = "";
					String preCondition = "", postCondition = "", invariantCondition = "", commitmentCondition = "", contextCondition = "";
					boolean canRepairing = false;

					for (int j = 0; j < propertyNodeList.getLength(); j++) {
						Node propertyNode = propertyNodeList.item(j);

						if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
							// System.err.println(propertyNode.getNodeName());
							// System.err.println(propertyNode.getTextContent());
							switch (propertyNode.getNodeName()) {
							case "parentGoal":
								parentGoal = propertyNode.getTextContent();
								break;
							case "decomposition":
								decomposition = Integer.parseInt(propertyNode
										.getTextContent());
								break;
							case "schedulerMethod":
								schedulerMethod = Integer.parseInt(propertyNode
										.getTextContent());
								break;
							case "priorityLevel":
								priorityLevel = Integer.parseInt(propertyNode
										.getTextContent());
								break;
							case "needDelegate":
								needDelegate = Boolean
										.parseBoolean(propertyNode
												.getTextContent());
								break;
							case "needPeopleInteraction":
								needPeopleInteraction = Boolean
										.parseBoolean(propertyNode
												.getTextContent());
								break;
							case "executingRequestedServiceName":
								executingRequestedServiceName = propertyNode
										.getTextContent();
								break;

							case "waitingTimeLimit":
								waitingTimeLimit = Integer
										.parseInt(propertyNode.getTextContent());
								break;

							case "Condition":
								// propertyNode又是一个有自己属性和子节点的节点
								// 先获得它的属性
								if (propertyNode.getAttributes().getLength() > 0) {
									for (int x = 0; x < propertyNode
											.getAttributes().getLength(); x++) {
										if (propertyNode.getAttributes()
												.item(x).getNodeName()
												.equals("type")) {
											switch (propertyNode
													.getAttributes().item(x)
													.getNodeValue()) {
											case "PRE":
												preCondition = "PRE";
												break;
											case "POST":
												postCondition = "POST";
												break;
											case "CONTEXT":
												contextCondition = "CONTEXT";
												break;
											case "COMMITMENT":
												commitmentCondition = "COMMITMENT";
												break;
											case "INVARIANT":
												invariantCondition = "INVARIANT";
												break;

											default:
												break;
											}
										}
									}
								}

								// 获得context节点的子节点
								NodeList conditionNodeList = propertyNode
										.getChildNodes();
								for (int y = 0; y < conditionNodeList
										.getLength(); y++) {
									Node conditionNode = conditionNodeList
											.item(y);
									if (conditionNode.getNodeType() == Node.ELEMENT_NODE) {
										switch (conditionNode.getNodeName()) {
										case "canRepairing":
											canRepairing = Boolean
													.parseBoolean(conditionNode
															.getTextContent());
											break;
										case "decription":

											break;
										case "relation":

											break;
										case "value":

											break;

										default:
											break;
										}
									}
								}

								break;

							default:
								break;
							}
						}
					}

					// 实例化一个element machine，根据不同的type实例不同的goal machine或者task
					// machine
					ElementMachine elementMachine = null;
					// 先找到对应的parent element machine，root goal的parent可以是null
					GoalMachine parentElementMachine = (GoalMachine) getElementMachineByName(
							goalModel.getElementMachines(), parentGoal);
					if (type.equals("GoalMachine")) {
						elementMachine = new GoalMachine(name, decomposition,
								schedulerMethod, parentElementMachine, level,
								needDelegate) {

							@Override
							public void checkPreCondition() {
								// TODO Auto-generated method stub

							}

							@Override
							public void checkPostCondition() {
								// TODO Auto-generated method stub

							}

							@Override
							public void checkInvariantCondition() {
								// TODO Auto-generated method stub

							}

							@Override
							public void checkContextCondition() {
								// TODO Auto-generated method stub

							}

							@Override
							public void checkCommitmentCondition() {
								// TODO Auto-generated method stub

							}
						};

					} else if (type.equals("TaskMachine")) {
						elementMachine = new TaskMachine(name,
								parentElementMachine, level,
								needPeopleInteraction) {

							@Override
							public void checkPreCondition() {
								// TODO Auto-generated method stub

							}

							@Override
							public void checkPostCondition() {
								// TODO Auto-generated method stub

							}

							@Override
							public void checkInvariantCondition() {
								// TODO Auto-generated method stub

							}

							@Override
							public void checkContextCondition() {
								// TODO Auto-generated method stub

							}

							@Override
							public void checkCommitmentCondition() {
								// TODO Auto-generated method stub

							}

						};
						// 不需要人的参与，而是需要调用服务的
						if (!needPeopleInteraction
								&& executingRequestedServiceName != "") {
							((TaskMachine) elementMachine)
									.setExecutingRequestedServiceName(executingRequestedServiceName);
						}
					}

					elementMachine.setDescription(description);
					if (waitingTimeLimit != 0) {
						elementMachine.setWaitingTimeLimit(waitingTimeLimit);
					}

					// 设置各种condition
					if (preCondition != "") {
						elementMachine.setPreCondition(new Condition("PRE",
								canRepairing));
					}
					if (postCondition != "") {
						elementMachine.setPostCondition(new Condition("POST"));
					}
					if (invariantCondition != "") {
						elementMachine.setInvariantCondition(new Condition(
								"INVARIANT"));
					}
					if (commitmentCondition != "") {
						elementMachine.setCommitmentCondition(new Condition(
								"COMMITMENT"));
					}
					if (contextCondition != "") {
						elementMachine.setContextCondition(new Condition(
								"CONTEXT"));
					}

					if (parentElementMachine != null) {
						parentElementMachine.addSubElement(elementMachine,
								priorityLevel);
					}
					goalModel.addElementMachine(elementMachine);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return goalModel;
	}

	/**
	 * 根据name在一个element machine列表中找到对应的元素
	 * 
	 * @param elementMachines
	 *            列表
	 * @param name
	 *            名字
	 * @return element machine
	 */
	private static ElementMachine getElementMachineByName(
			ArrayList<ElementMachine> elementMachines, String name) {
		ElementMachine em = null;
		if ((name != null || name != "")
				&& (elementMachines != null || elementMachines.size() != 0)) {
			for (ElementMachine elementMachine : elementMachines) {
				if (elementMachine.getName().equals(name)) {
					em = elementMachine;
					break;
				}
			}
		}
		return em;
	}

}
