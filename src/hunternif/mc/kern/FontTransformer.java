package hunternif.mc.kern;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class FontTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (name.equals("net.minecraft.client.gui.FontRenderer")) {
			log("Inspecting class FontRenderer");
			
			ClassReader cr = new ClassReader(bytes);
			ClassNode classNode = new ClassNode();
			cr.accept(classNode, 0);
			
			for (MethodNode method : classNode.methods) {
				if (method.name.equals("renderStringAtPos")) {
					log("Inspecting method renderStringAtPos");
					/* Need to replace
					 * 
					 * FLOAD 9: f1
				     * FCONST_1
				     * FADD
				     * 
				     * with
				     * 
				     * FLOAD 9: f1
				     * FLOAD 7: f
				     * FADD
					 */
					for (int i = 0; i < method.instructions.size(); i++) {
						AbstractInsnNode node = method.instructions.get(i);
						
						// Look for the FCONST_1 node:
						if (node.getOpcode() == Opcodes.FCONST_1) {
							log("Found FCONST_1");
							// Check that the previous node is FLOAD 9:
							AbstractInsnNode prevNode = method.instructions.get(i - 1);							
							if (prevNode.getOpcode() != Opcodes.FLOAD ||
									prevNode.getType() != AbstractInsnNode.VAR_INSN ||
									((VarInsnNode)prevNode).var != 9) {
								continue;
							}
							log("Found FLOAD 9");
							
							// Check that the next node is FADD:
							AbstractInsnNode nextNode = method.instructions.get(i + 1);
							
							if (nextNode.getOpcode() != Opcodes.FADD) {
								continue;
							}
							log("Found FADD");
							
							// Replace FCONST_1 with FLOAD 7:
							method.instructions.set(node, new VarInsnNode(Opcodes.FLOAD, 7));
							
							log("Transform 1 completed");
							break;
						}
					}
				} else if (method.name.equals("getStringWidth")) {
					log("inspecting method getStringWidth");
					/* Need to replace
					 * 
					 * IINC 2: i
					 * 
					 * with
					 * 
					 * ILOAD 2: i
					 * ALOAD 0: this
					 * GETFIELD net/minecraft/client/gui/FontRenderer.unicodeFlag : boolean
					 * IFEQ L24
					 * ICONST_0
					 * GOTO L25
					 * L24
					 * ICONST_1
					 * L25
					 * IADD
					 * ISTORE 2: i
					 */
					for (int i = 0; i < method.instructions.size(); i++) {
						AbstractInsnNode node = method.instructions.get(i);
						
						if (node.getOpcode() != Opcodes.IINC ||
								node.getType() != AbstractInsnNode.IINC_INSN ||
								((IincInsnNode)node).var != 2) {
							continue;
						}
						log("Found IINC 2");
						
						// Create new instructions:
						LabelNode label1 = new LabelNode(new Label());
						LabelNode label2 = new LabelNode(new Label());
						
						InsnList toInject = new InsnList();
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 2));
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/FontRenderer", "unicodeFlag", Type.BOOLEAN_TYPE.getDescriptor()));
						toInject.add(new JumpInsnNode(Opcodes.IFEQ, label1));
						toInject.add(new InsnNode(Opcodes.ICONST_0));
						toInject.add(new JumpInsnNode(Opcodes.GOTO, label2));
						toInject.add(label1);
						toInject.add(new InsnNode(Opcodes.ICONST_1));
						toInject.add(label2);
						toInject.add(new InsnNode(Opcodes.IADD));
						toInject.add(new VarInsnNode(Opcodes.ISTORE, 2));
						
						// Insert new instructions after this one:
						method.instructions.insert(node, toInject);
						
						// Remove the old instruction:
						method.instructions.remove(node);
						
						log("Transform 2 completed");
						break;
					}
				}
			}
			
			ClassWriter cw = new ClassWriter(cr, 0);
			classNode.accept(cw);
			return cw.toByteArray();
		}
		return bytes;
	}
	
	/** A simple dirty logger method because I couldn't find a proper logger.
	 * Appends "[BetterKerning ASM] " to the message and prints it to System.out
	 */
	private static void log(String message) {
		System.out.println("[BetterKerning ASM] " + message);
	}

}
