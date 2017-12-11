package org.example.zzzyxwvut.plankwalker;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * The <code>PlankWalker</code> enum exhibits the circumstances
 * under which the <code>finally</code> block is out of reach.
 *
 * @author	Aliaksei Budavei
 * @version	0.1
 */
public enum PlankWalker
{
	/** Do nothing. */
	BLACKOUT
	{
		@Override public void croak() { }	/* NOP */
	},

	/** Spawn a daemon thread. */
	DAEMON
	{
		@Override public void croak()
		{
			/*
			 * "When the JVM halts, any remaining daemon
			 * threads are abandoned--_finally_ blocks are
			 * not executed, stacks are not unwound--the
			 * JVM just exits."
			 *
			 * Brian Goetz et al. "Java Concurrency in
			 *	Practice", 7.4.2. Daemon Threads.
			 *
			 *
			 * "You should be aware that daemon threads
			 * will terminate their run() methods without
			 * executing _finally_ clauses."
			 *
			 * Bruce Eckel "Thinking in Java" (4th Ed.),
			 * Concurrency, Basic threading, Daemon threads.
			 */

			Thread daemon	= new Thread() {
				@Override
				public void run()
				{
					try {
						System.err.println("BEGIN:\t"
							+ Thread.currentThread());

						/* Allow the main thread to finish. */
						TimeUnit.MILLISECONDS.sleep(60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						System.err.println("END:\t"
							+ Thread.currentThread());
					}
				}
			};

			daemon.setDaemon(true);
			daemon.start();
			System.err.println("Abandon daemon thread");
		}
	},

	/** Invoke the <code>Runtime.exit</code> method. */
	EXIT
	{
		@Override public void croak()
		{
			/*
			 * "The Java Virtual Machine exits when some
			 * thread invokes the _exit_ method of class
			 * Runtime or class System, or the _halt_ method
			 * of class Runtime, and the exit or halt
			 * operation is permitted by the security
			 * manager."
			 *
			 * Tim Lindholm et al. "The Java Virtual
			 * Machine Specification. Java SE 9 Edition",
			 * 5.7 Java Virtual Machine Exit.
			 */
			System.err.println("Abort via Runtime.exit(int)");
			Runtime.getRuntime().exit(4);
		}
	},

	/** Invoke the <code>Runtime.halt</code> method. */
	HALT
	{
		@Override public void croak()
		{
			/* See the PlankWalker.EXIT comment above. */
			System.err.println("Abort via Runtime.halt(int)");
			Runtime.getRuntime().halt(8);
		}
	},

	/**
	 * On a GNU/Linux system attempt to terminate the JVM
	 * via the native kill(2) system call.
	 */
	KILL
	{
		@Override public void croak()
		{
			if (!System.getProperty("os.name")
					.equalsIgnoreCase("linux"))
				return;

			try {
				/*
				 * Resolve the _self_ link to the pid directory.
				 * See proc(5).
				 *
				 * Note: gij (GNU libgcj) v6.3.0 appears to trap
				 *	SIGTERM on its own pid.
				 */
				System.err.println("Abort via the kill(2) syscall");
				Runtime.getRuntime().exec("/bin/kill -TERM " +
						new File("/proc/self")
						.getCanonicalFile().getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/** Makes the JVM instance terminate. */
	public abstract void croak();
}
