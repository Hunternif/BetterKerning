package hunternif.mc.kern;

import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class FontTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (name.equals("net.minecraft.client.gui.FontRenderer")) {
			ClassReader cr = new ClassReader(bytes);
			ClassNode classNode = new ClassNode();
			cr.accept(classNode, 0);
			
			for (MethodNode method : classNode.methods) {
				if (method.name.equals("renderStringAtPos")) {
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
					ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
					while (iter.hasNext()) {
						AbstractInsnNode node = iter.next();
						// Look for the FCONST_1 node:
						if (node.getOpcode() == Opcodes.FCONST_1 && iter.hasPrevious()) {
							// Check that the previous node is FLOAD 9:
							AbstractInsnNode prevNode = iter.previous();
							iter.next(); // Move the iterator back
							if (prevNode.getOpcode() != Opcodes.FLOAD ||
									prevNode.getType() != AbstractInsnNode.VAR_INSN ||
									((VarInsnNode)prevNode).var != 9) {
								continue;
							}
							// Check that the next node is FADD:
							AbstractInsnNode nextNode = iter.next();
							iter.previous(); // Move the iterator back
							if (prevNode.getOpcode() != Opcodes.FADD) {
								continue;
							}
							iter.remove();
							// Replace FCONST_1 with FLOAD 7:
							iter.set(new VarInsnNode(Opcodes.FLOAD, 7));
						}
					}
				}
			}
			
			ClassWriter cw = new ClassWriter(cr, 0);
			classNode.accept(cw);
			return cw.toByteArray();
		}
		return bytes;
	}

}
