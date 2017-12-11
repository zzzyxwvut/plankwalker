package org.example.zzzyxwvut.plankwalker;

/**
 * The entry point class.
 *
 * @author	Aliaksei Budavei
 * @version	0.1
 */
public final class MainLauncher
{
	private static final PlankWalker[] all	= PlankWalker.values();
	private static final int size		= all.length;

	private MainLauncher() { }

	public static void main(String[] args)
	{
		try {
			System.err.println("BEGIN:\t" + Thread.currentThread());
			int value	= 0;	/* Generate or fetch a number. */

			if (args.length > 0) {
				try {
					value	= Integer.parseInt(args[0]);

					if (value < 0 || value > size - 1)
						throw new NumberFormatException();
				} catch (NumberFormatException e) {
					System.err.println("Out of range value: [0-"
							+ (size - 1) + "]\n");
					value	= (int) (Math.random() * size);
				}	/* Funnel invalid and out-of-range values. */
			} else {
				value	= (int) (Math.random() * size);
			}

			MainLauncher.all[value > -1 && value < size ? value : 0].croak();
		} finally {
			System.err.println("END:\t" + Thread.currentThread());
		}
	}
}
