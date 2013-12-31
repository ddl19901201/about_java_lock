package condi;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockCondition {
	// ʵ����һ��������
	final Lock lock = new ReentrantLock();
	// ʵ��������condition
	final Condition notFull = lock.newCondition(); 
	final Condition notEmpty = lock.newCondition();

	final Object[] items = new Object[3];// ��ʼ��һ������Ϊ100�Ķ���
	int putptr, takeptr, count;

	public void put(Object x) throws InterruptedException {
		lock.lock();// ��ȡ��
		try {
			while (count == items.length){
				notFull.await();// ��������count���ڶ��еĳ���ʱ�������ڲ��룬��˵ȴ�
			}
			items[putptr] = x; // ���������putptr������
			if (++putptr == items.length){
				putptr = 0;// ���������ȵ��ڶ��г���ʱ����putptr��Ϊ0
			}			
			// ԭ���ǣ�����Խ�����
			++count;// û����һ������ͽ���������1
			notEmpty.signal();// һ������ͻ���ȡ�����߳�
		} finally {
			lock.unlock();// ����ͷ���
		}
	}

	public Object take() throws InterruptedException {
		lock.lock();// ��ȡ��
		try {
			while (count == 0){
				// �������������0��ô�ȴ�
				notEmpty.await();
			}	
			Object x = items[takeptr]; // ȡ��takeptr����������
			if (++takeptr == items.length){
				takeptr = 0;// ��takeptr�ﵽ���г���ʱ�����㿪ʼȡ
			}
			--count;// ÿȡһ������������1
			notFull.signal();// öȡ��һ���ͻ��Ѵ��߳�
			return x;
		} finally {
			lock.unlock();// �ͷ���
		}
	}
	public static void main(String[] args) throws Exception {
		LockCondition c = new LockCondition();
		c.put("iamzhongyong");
		c.put("bixiao");
		c.put("zhongyong");
		System.out.println(c.take());
		System.out.println(c.take());
		System.out.println(c.take());
		c.take();

	}
}
