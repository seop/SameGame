package com.github.seop;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Random;

//그리기 전담
class Draw extends Canvas  {
	private static final long serialVersionUID = 1L;
	private boolean start = false; //게임 시작 여부
	private int s_xpos = 0, s_ypos = 0;
	//BOXWIDTH 상자 너비, COL 열 갯수, ROW 행 갯수
	private final int BOXWIDTH = 40, COL = 20, ROW = 14;
	private int[][] box = new int[COL][ROW];
	private boolean[][] boxx = new boolean[COL][ROW];
	private Color[] boxColor = new Color[13];
	private int x2 = -1, y2 = -1;
	private Image image;
	private Graphics buff = null;
	
	public Draw() {
		for(int i = 0; i < COL; i++) {
			for(int j = 0; j < ROW; j++) {
				//상자는 여섯개의 값을 가진다. 여섯가지 색을 가짐
				box[i][j] = new Random().nextInt(6) + 1;
				boxx[i][j] = false;
			}
		}
		boxColor[0] = new Color(0xffffff);
		boxColor[1] = new Color(255, 72, 72);
		boxColor[2] = new Color(150, 105, 254);
		boxColor[3] = new Color(35, 129, 156);
		boxColor[4] = new Color(31, 203, 74);
		boxColor[5] = new Color(182, 186, 24);
		boxColor[6] = new Color(128, 0, 128);
		
		//아래는 선택되어 있을 때 변환되는 색상
		boxColor[7] = new Color(255, 206, 206);
		boxColor[8] = new Color(221, 206, 255);
		boxColor[9] = new Color(184, 226, 239);	
		boxColor[10] = new Color(214, 248, 222);
		boxColor[11] = new Color(247, 249, 208);
		boxColor[12] = new Color(255, 206, 255);
	}

	public void update(Graphics g) {
		paint(g);
	}
	
	public void paint(Graphics gg) {
		Graphics2D g = (Graphics2D)gg;
		image = this.createImage(this.getWidth(), this.getHeight());
		buff = image.getGraphics();

		buff.setColor(new Color(0xf6f6f6));
		for(int x = s_xpos; x <= BOXWIDTH * COL + s_xpos; x += BOXWIDTH) {
			buff.drawLine(x, s_ypos, x, ROW * BOXWIDTH + s_ypos);
		}
		for(int y = s_ypos; y <= BOXWIDTH * ROW + s_ypos; y += BOXWIDTH) {
			buff.drawLine(s_xpos, y, COL * BOXWIDTH + s_xpos, y);
		}

		//box[x][y] 값을 바탕으로 해당 색의 상자를 그림
		for(int y = s_ypos+1, yy = 0; y < BOXWIDTH * ROW + s_ypos; y += BOXWIDTH, yy++) {
			for(int x = s_xpos+1, xx = 0; x < BOXWIDTH * COL + s_xpos; x += BOXWIDTH, xx++) {
				if(box[xx][yy] != 0) {
					buff.setColor(boxColor[box[xx][yy]]);
					buff.fill3DRect(x, y, BOXWIDTH-1, BOXWIDTH-1, true);
				}
			}		
		}
		
		g.drawImage(image, 0, 0, this);
		
	}

	//클릭했을 경우
	public boolean setBox(int xx, int yy) {
		boolean bl = true;
		if(start) {
			int x = (xx - s_xpos) / BOXWIDTH;
			int y = (yy - s_ypos) / BOXWIDTH;
			if(xx >= s_xpos && yy >= s_ypos && x >= 0 && x < COL && y >= 0 && y < ROW && box[x][y] != 0 && this.countTrue() >= 2) {
				this.checkBox();//클릭해서 상자를 없애고 난 후 해야할 일. 상자정렬
				this.repaint();
				Toolkit.getDefaultToolkit().beep();
				bl = this.checkBox2();//지울 수 있는 상자가  존재하는지 여부 확인
			}
			x2 = -1;
			y2 = -1;
			//		this.searchBox(xx, yy);
		}
		return bl;
	}
	//같은 값을 가지는 상자가 인접해 있는지.
	private int countTrue() {
		int count = 0;
		for(int i = 0; i < COL; i++) {
			for(int j = 0; j < ROW; j++) {
				if(boxx[i][j] == true) {
					count++;
				}
			}
		}
		return count;
	}
	//클릭 후 상자를 제거한 후 해야 할 일
	public void checkBox() {
		//제거해야 할 상자의 좌표를 boxx[][] 에 true로 했으므로,  
		//box[][] 값을 0으로 줘서 빈상자로 만듦. 그런 후 boxx[][] 값을 false로 바꿈.
		for(int i = 0; i < COL; i++) {
			for(int j = 0; j < ROW; j++) {
				if(boxx[i][j] == true) {
					boxx[i][j] = false;
					box[i][j] = 0;
				}
			}
		}
		//맨 아래 쪽 행부터 빈상자가 존재하는지 검사. 발견하면 윗 상자와 값 교환.
		for(int p = 0; p < 5; p++) {
			for(int y = ROW-1; y > 0; y--) {
				for(int x = 0; x < COL; x++) {
					if(box[x][y] == 0) {
						box[x][y] = box[x][y-1];
						box[x][y-1] = 0;
					}
				}
			}
		}
		//모두 비어있는 열을 검사. 발견하면 우측 열과 교환.
		for(int p = 0; p < 3; p++) {
			for(int x = 0; x < COL-1; x++) {
				if(box[x][ROW-1] == 0) {
					for(int y = ROW-1; y >= 0; y--) {
						int i = box[x][y];
						box[x][y] = box[x+1][y];
						box[x+1][y] = i;
					}
				}
			}
		}
	}
	//지울 수 있는 상자가 존재하는지 여부  검사. 값은 값을 가진 상자가 인접해 있는 경우가 단 한가지라도 있으면 true
	private boolean checkBox2() {
		boolean bl = false;
			for(int x = 0; x < COL; x++) {
				for(int y = 0; y < ROW-1; y++) {
					if(box[x][y] != 0) {
						if(box[x][y] == box[x][y+1]) {
							return true;
						}
					}
				}
			}
			for(int y = 0; y < ROW; y++) {
				for(int x = 0; x < COL-1; x++) {
					if(box[x][y] != 0) {
						if(box[x][y] == box[x+1][y]) {
							return true;
						}
					}
				}
			}
		return bl; 
	}
	
	//마우스 포인터가 움직일 때마다, 제거 가능한 둘 이상의 상자를 찾음.
	public void searchBox(int xx, int yy) {
		if(start) {
			int x = (xx - s_xpos) / BOXWIDTH;
			int y = (yy - s_ypos) / BOXWIDTH;
			if(xx >= s_xpos && yy >= s_ypos && x >= 0 && x < COL && y >= 0 && y < ROW) {
				if(x != x2 || y != y2) {
					x2 = x;
					y2 = y;
					//이전에 선택되었던 상자를 원래 값으로 변환. 색을 변화시키기 위해 값을 조정했으므로.
					this.undoPreviousSelectedBox(this.countTrue());
					this.clearBoxx();// boxx[][]를 모두 false로.
					this.repaint();

					if(box[x][y] != 0) {
						boxx[x][y] = true;
					}
					this.searchSameBox();
					this.repaint();
				}
			} else {
				this.undoPreviousSelectedBox(this.countTrue());
				this.clearBoxx();
				this.repaint();
				x2 = -1;
				y2 = -1;
			}
		}
	}
	//같은 값을 가진 상자가 인접해 있는지 검색한다. 본 프로그램의 핵심부분.
	private void searchSameBox() {
		for(int t = 0; t < 4; t++) {// 몇차례 검색을 반복한다. true값이 늘어나므로..
			for(int x = 0; x < COL; x++) {
				for(int y = 0; y < ROW; y++) {
					if(boxx[x][y] == true) {//마우스 포인터가 올려진 상자가 최초의 true값 가짐
						int i = x, j = y;
						//서쪽방향 검사
						while(i > 0) {
							if(box[--i][y] == box[x][y]) {
								boxx[i][y] = true;//같은 값을 가진 상자 발견하면 true.
							} else {
								break;
							}
						}
						i = x;
						//동쪽방향 검사
						while(i < COL-1) {
							if(box[++i][y] == box[x][y]) {
								boxx[i][y] = true;
							} else {
								break;
							}
						}
						 j = y;
						 //북쪽방향 검사
						while(j > 0) {
							if(box[x][--j] == box[x][y]) {
								boxx[x][j] = true;
							} else {
								break;
							}
						}
						j = y;
						//남쪽방향 검사
						while(j < ROW-1) {
							if(box[x][++j] == box[x][y]) {
								boxx[x][j] = true;
							} else {
								break;
							}
						}
					}
				}
			}
		}
		this.selectedBox(this.countTrue());
	}
	private void undoPreviousSelectedBox(int truecount) {
		if(truecount >= 2) {
			for(int i = 0; i < COL; i++) {
				for(int j = 0; j < ROW; j++) {
					if(boxx[i][j] == true) {
						box[i][j] -= 6;
					}
				}
			}
		}
	}
	private void selectedBox(int truecount) {
		if(truecount >= 2) {
			for(int i = 0; i < COL; i++) {
				for(int j = 0; j < ROW; j++) {
					if(boxx[i][j] == true) {
						box[i][j] += 6;
					}
				}
			}
		}
	}
	private void clearBoxx() {
		for(int x = 0; x < COL; x++) {
			for(int y = 0; y < ROW; y++) {
				boxx[x][y] = false;
			}
		}
	}
	public int getCountBox() {
		int tmp = 0;
		for(int i = 0; i < COL; i++) {
			for(int j = 0; j < ROW; j++) {
				if(box[i][j] == 0) {
					tmp++;
				}
			}
		}
		return ROW*COL - tmp;
	}
	public void setStart() {
		start = true;
	}
	public void setTimeOver() {
		start = false;
	}
	public void setRestart() {
		for(int i = 0; i < COL; i++) {
			for(int j = 0; j < ROW; j++) {
				box[i][j] = new Random().nextInt(6) + 1;
				boxx[i][j] = false;
			}
		}
		this.repaint();
		start = true;
	}
}
