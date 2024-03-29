import java.util.ArrayList;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import tanks.common.Drawable;
import tanks.common.Renderer;

import com.jogamp.opengl.util.FPSAnimator;

public class ScorchedRenderer extends GLCanvas implements GLEventListener, Renderer
{
	
	private static final long serialVersionUID = -8513201172428486833L;
	public float viewWidth, viewHeight;
	public float screenWidth, screenHeight;
    
    private static ScorchedRenderer instance = null;
    
	public long timeSinceLastDraw;
	private long lastDrawTime;
	
	private FPSAnimator animator;
	
	private ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	private boolean didInit = false;
    
    public static ScorchedRenderer getInstance()
    {
    	if(instance == null) instance = new ScorchedRenderer();
    	return instance;
    }
    
    public ScorchedRenderer()
    {
		// setup OpenGL Version 2
    	super(new GLCapabilities(GLProfile.get(GLProfile.GL2)));

		this.addGLEventListener(this);
		this.setSize( 1800, 1000 );
		
		animator = new FPSAnimator(this, 60);
		animator.start();
    }
    
    public void registerDrawable(Drawable d)
    {
    	drawables.add(d);
    }
    
	public void unregisterDrawable(Drawable d) 
	{
		drawables.remove(d);
	}

	public void init(GLAutoDrawable d) 
	{
		final GL2 gl = d.getGL().getGL2();
		gl.glClearColor(0f, 0f, 0f, 1f);
		
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_BLEND);
	}

	public void display(GLAutoDrawable d) 
	{
		final GL2 gl = d.getGL().getGL2();
		timeSinceLastDraw = System.currentTimeMillis()-lastDrawTime;
		lastDrawTime = System.currentTimeMillis();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glActiveTexture(GL2.GL_TEXTURE0);
        
		gl.glLoadIdentity();
		
		for(int i=0; i < drawables.size(); i++) {
	    	if(!drawables.get(i).didInit) drawables.get(i).init(d, viewWidth, viewHeight);
			drawables.get(i).display(d, this);
		}
	}
	
	public void reshape(GLAutoDrawable d, int x, int y, int width, int height) 
	{
		final GL2 gl = d.getGL().getGL2();
	    gl.glViewport(0, 0, width, height);
	    float ratio = (float) height / width;
	    
	    screenWidth = width;
	    screenHeight = height;
        viewHeight = 100;
        viewWidth = 175;
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity ();
        (new GLU()).gluOrtho2D(-viewWidth/2, viewWidth/2, -viewHeight/2, (ratio*viewWidth + (ratio*viewWidth-viewHeight))/2);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        
	    if(!didInit ) {
	    	didInit = true;
	    	for(int i=0; i < drawables.size(); i++) drawables.get(i).init(d, viewWidth, viewHeight);
	    }
	    else {
	    	for(int i=0; i < drawables.size(); i++) drawables.get(i).reshape(d, x, y, (int)viewWidth, (int)viewHeight);
	    }
	}
	
	public void render(GL2 gl, int draw_mode, Drawable thing)
	{
		gl.glColor3f(1, 1, 1);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, thing.getGeometry());
		gl.glDrawArrays(draw_mode, 0, thing.getNumPoints());
	}
	
	public void screenToViewCoords(float[] xy)
	{
		float viewX = (xy[0]/screenWidth)*viewWidth-viewWidth/2;
		float viewY = -(xy[1]/screenHeight)*viewHeight+viewHeight/2;
		xy[0] = viewX;
		xy[1] = viewY;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) 
	{
		
	}

	@Override
	public long getTimeSinceLastDraw() 
	{
		return timeSinceLastDraw;
	}
}