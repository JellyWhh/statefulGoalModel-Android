/**
 * 
 */
package edu.fudan.agent.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.fudan.se.agent.data.UserInformation;

/**
 * 这个类里面的静态方法都是在AideAgent中用到的
 * 
 * @author whh
 * 
 */
public class AideAgentSupport {
	/**
	 * 根据抽象服务名称，从所有服务列表中查找出可用的具体服务名称
	 * 
	 * @param abstractServiceName
	 *            抽象服务名称
	 * @return 与抽象服务名称对应的所有可用的具体服务名称列表
	 */
	public static ArrayList<String> getServiceNameListBasedAbstractServiceName(
			String abstractServiceName,
			ArrayList<String> allIntentServiceNameArrayList) {
		ArrayList<String> ret = new ArrayList<>();

		for (String intentService : allIntentServiceNameArrayList) {
			if (intentService.contains(abstractServiceName)) {
				ret.add(intentService);
			}
		}

		return ret;
	}

	/**
	 * 获得一个goalModel-element对应的服务调用情况
	 * 
	 * @param goalModelName
	 *            goal model name
	 * @param elementName
	 *            element name
	 * @return 服务调用情况
	 */
	public static ServiceInvocationUtil getServiceInvocationUtil(
			String goalModelName, String elementName,
			ArrayList<ServiceInvocationUtil> serviceInvocationUtilList) {
		ServiceInvocationUtil ret = null;
		for (ServiceInvocationUtil serviceInvocationUtil : serviceInvocationUtilList) {
			if (serviceInvocationUtil.getGoalModelName().equals(goalModelName)
					&& serviceInvocationUtil.getElementName().equals(
							elementName)) {
				ret = serviceInvocationUtil;
				break;
			}
		}
		return ret;
	}

	/**
	 * 初始化所有可能用到的intent service
	 */
	public static void initAllIntentService(
			ArrayList<String> allIntentServiceNameArrayList) {
		allIntentServiceNameArrayList
				.add("service.intentservice.weatherCandidate");
		allIntentServiceNameArrayList.add("service.intentservice.weather");
		allIntentServiceNameArrayList.add("service.intentservice.setcityname");
		allIntentServiceNameArrayList.add("service.intentservice.readimage");
		allIntentServiceNameArrayList.add("service.intentservice.showcontent");
		allIntentServiceNameArrayList.add("service.intentservice.takepicture");
		allIntentServiceNameArrayList.add("service.intentservice.userinput");
	}

	/**
	 * 返回两个人的亲密度
	 * 
	 * @param self
	 *            我自己
	 * @param friend
	 *            我朋友
	 * @return 朋友与自己的亲密度
	 */
	private static int getIntimacy(String self, String friend) {
		return 1;
	}

	/**
	 * 获得最佳可委托对象的agent nick name
	 * 
	 * @param userInformations
	 *            所有可委托对象的userInformation
	 * @param selfLocation
	 *            自己的位置
	 * @param selfAgentNickName
	 *            自己的agent nick name
	 * @return 最佳可委托对象的agent nick name
	 */
	public static String getDelegateToBasedRanking(
			ArrayList<UserInformation> userInformations, String selfLocation,
			String selfAgentNickName) {

		// 获取与每个人的亲密度
		for (UserInformation userInformation : userInformations) {
			userInformation.setIntimacy(getIntimacy(selfAgentNickName,
					userInformation.getUserAgentNickname()));
		}

		Map<String, Double> distance = new HashMap<String, Double>();

		// LatLng selfLatLng = getLatLng(sgmApplication.getLocation());
		// 获取与所有好友距离的最大距离和最小距离
		double maxDis = 0, minDis = 0;
		boolean isFirst = true;
		for (UserInformation userInformation : userInformations) {
			// double dis = DistanceUtil.getDistance(selfLatLng,
			// getLatLng(userInformation.getLocation()));
			double dis = getShortDistance(selfLocation,
					userInformation.getLocation());
			if (isFirst) {
				maxDis = dis;
				minDis = dis;
				isFirst = false;
			} else {
				if (maxDis < dis) {
					maxDis = dis;
				}
				if (minDis > dis) {
					minDis = dis;
				}
			}
		}
		// 对位置距离进行归一化，然后算与所有好友的“距离”
		for (UserInformation userInformation : userInformations) {
			double locationDis = 0;
			if (maxDis != minDis) {
				locationDis = (getShortDistance(selfLocation,
						userInformation.getLocation()) / (maxDis - minDis));
			}
			double dis = (locationDis + userInformation.getReputation() + userInformation
					.getIntimacy()) / 3;
			distance.put(userInformation.getUserAgentNickname(), dis);
		}

		List<Map.Entry<String, Double>> sortList = new ArrayList<Map.Entry<String, Double>>(
				distance.entrySet());
		Collections.sort(sortList, new Comparator<Map.Entry<String, Double>>() {
			/**
			 * 按照距离从小到大排序
			 * 
			 * @param lhs
			 * @param rhs
			 * @return
			 */
			@Override
			public int compare(Entry<String, Double> lhs,
					Entry<String, Double> rhs) {
				if (lhs.getValue() == rhs.getValue()) {
					return 0;
				} else if (lhs.getValue() > rhs.getValue()) {
					return 1;
				} else {
					return -1;
				}
			}

		});

		// 返回排在第一个的
		return sortList.get(0).getKey();
	}

	/**
	 * 根据位置中的经纬度计算出距离
	 * 
	 * @param location1
	 *            位置1
	 * @param location2
	 *            位置2
	 * @return 两个位置之间的距离
	 */
	private static double getShortDistance(String location1, String location2) {

		double lon1 = Double
				.parseDouble(location1.split("\n")[2].split(":")[1]);
		double lat1 = Double
				.parseDouble(location1.split("\n")[3].split(":")[1]);

		double lon2 = Double
				.parseDouble(location2.split("\n")[2].split(":")[1]);
		double lat2 = Double
				.parseDouble(location2.split("\n")[3].split(":")[1]);

		double a, b, R;
		R = 6378137; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (lon1 - lon2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2
				* R
				* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
						* Math.cos(lat2) * sb2 * sb2));
		return d;
	}
}
