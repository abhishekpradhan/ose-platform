package ose.processor.splock;

import java.io.IOException;

import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;

public class LeftPushPhraseNode extends LeftPushConjunctiveNode {
	
	private int currentPosition;
	
	public LeftPushPhraseNode() {
		super();
		nodeName = "PhraseL";
	}
	
	@Override
	public SpanStatus nextDoc() throws IOException {
		while (true){
			status = super.nextDoc();
			if (status.isAvailable()){
				currentPosition = -1;
				if (nextSpan(false)){
					return status;
				}
			}
			else
				return status;
		}
	}
	
	@Override
	public boolean nextSpan() throws IOException {
		return nextSpan(true);
	}
	
	public boolean nextSpan(boolean needToAdvance) throws IOException {
		if (needToAdvance)
			currentPosition += 1;
		currentSpan = null;
		while (true){
			TreeNode firstChild = children.get(0);
			while (firstChild.getCurrentSpan() != null && firstChild.getCurrentSpan().startPos < currentPosition){
				if (!firstChild.nextSpan())
					return false;
			}
			if (firstChild.getCurrentSpan() == null)
				return false;
			currentPosition = firstChild.getCurrentSpan().startPos;
			int i = 0;
			for (i = 1; i < children.size(); i++) {
				TreeNode child = children.get(i);
				while (child.getCurrentSpan() != null && child.getCurrentSpan().startPos < currentPosition + i){
					if (!child.nextSpan())
						return false;
				}
				if (child.getCurrentSpan() == null)
					return false;
				if (child.getCurrentSpan().startPos != currentPosition + i){
					currentPosition = child.getCurrentSpan().startPos - i;
					break;
				}
			}
			if (i == children.size()){
				setCurrentSpan();
				return true;
			}
		}
	}
	
	private void setCurrentSpan() {
		int maxEndPos = 0;
		for (TreeNode child : children){
			if (child.getCurrentSpan().endPos > maxEndPos)
				maxEndPos = child.getCurrentSpan().endPos ;
		}
		currentSpan = new Span(status.docId, currentPosition,maxEndPos);
	}
	
	@Override
	public SpanStatus skipTo(int target) throws IOException {
		status = super.skipTo(target);
		if (status.isAvailable()){
			currentPosition = -1;
			if (nextSpan(false)){
				return status;
			}
			status = nextDoc();
		}
		
		return status;
	}
	
	@Override
	public SpanStatus lazySkipTo(int target) throws IOException {
		status = super.lazySkipTo(target);
		if (status.isAvailable()){
			currentPosition = -1;
			if (nextSpan(false)){
				return status;
			}
			status.setStatus(SpanStatus.STATUS_NO_MORE_SPAN);
			return status;
		}
		
		return status;
	}
}
