/**
 * Copyright (c) 2017, UCLA Software Engineering and Analysis Laboratory (SEAL)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package ut.seal.plugins.utils;

import org.apache.commons.exec.CommandLine;

/**
 * @author Myoungkyu Song
 * @date Oct 28, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCCFinder extends UTCCFinderAbstract {

	public static void runCcfinder(String ccfinder, String oFile, //
			String target, String lFile, String rFile) {
		CommandLine cmdLine = new CommandLine(ccfinder);
		cmdLine.addArgument("d");
		cmdLine.addArgument("java");
		cmdLine.addArgument("-b"); // The minimum length of the detected code clones
		cmdLine.addArgument("3");
		cmdLine.addArgument("-o");
		cmdLine.addArgument(target + oFile);
		cmdLine.addArgument(target + lFile);
		cmdLine.addArgument("-is");
		cmdLine.addArgument(target + rFile);
		cmdLine.addArgument("-w");
		cmdLine.addArgument("f-w-g+"); // the last -w w-f-g+ means "do not detect code clones within a file", "do not detect code clones between files in the same file group", and "detect code clones between files from the distinct file groups".

		invokeCmd(cmdLine);

		cmdLine = new CommandLine(ccfinder);
		cmdLine.addArgument("p");
		cmdLine.addArgument(target + "a.ccfxd");

		invokeCmd(cmdLine);
	}
}