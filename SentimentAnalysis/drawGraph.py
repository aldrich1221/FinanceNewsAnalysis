import plotly.express as px
import numpy as np
import os

if not os.path.exists("images"):
    os.mkdir("images")
import logging
import boto3
from botocore.exceptions import ClientError



def upload_file(file_name, bucket,object_name):
    
   
    if object_name is None:
        object_name = file_name

   
    s3_client = boto3.client('s3',aws_access_key_id="XXX",
    							aws_secret_access_key="XXX")
    try:
        response = s3_client.upload_file(file_name, bucket, object_name,ExtraArgs={'ACL': 'public-read'})
    except ClientError as e:
        logging.error(e)
        return False
    return True

import os
allFileNum = 0
def printPath(level, path):
	print("Data Prcessing...")
	allFileNum=0
	dirList = []
	fileList = []
	files = os.listdir(path)
	dirList.append(str(level))
	positiveNewRatio=list()
	negativeNewRatio=list()
	uncertaintyNewRatio=list()
	DateList=list()
	import pandas as pd
	for f in files:
		if(os.path.isdir(path + '/' + f)):
			if(f[0] == '.'):
				pass
			else:
				dirList.append(f)
		if(os.path.isfile(path + '/' + f)):
			fileList.append(f)
	
	dataArray=[[0,0,0]]
	datelist=list()
	for fl in range(len(fileList)):

		filename=path+"/"+fileList[fl]
		FileDate=fileList[fl].replace(".txt","").replace("Result","")[1:]
		fr = open(filename,'r')
	
		datalist=list()
		indexlist=list()
		linecount=0

		for line in fr.readlines():

			string=line.replace("\n","").split("\t")

			datalist.append(float(string[1]))
			indexlist.append(string[0])
			


		total=datalist[1]+datalist[2]+datalist[3]
		
		dataArray=np.vstack([dataArray,[int(datalist[1]/total*100),int(datalist[2]/total*100),int(datalist[3]/total*100)]])
		datelist.append(FileDate)
		
	
	dataframe=pd.DataFrame(dataArray[1:],columns=[indexlist[1],indexlist[2],indexlist[3]])
	dataframe['Date']=datelist
	finalFrame=dataframe.sort_values(by=['Date'])
	
	print("Data Prcessing completed!")
	print(finalFrame.head(4))
	return finalFrame




def DrawGraph(dataframe):
	import plotly.graph_objects as go
	import numpy as np
	print("begin to draw grpah...")
	
	title = 'Sentiment Analysis over TASLA News'
	labels = ['negative','positive','uncertainty']
	colors = ['rgb(67,255,111)', 'rgb(255,115,115)', 'rgb(49,0,189)']

	mode_size = [8, 8, 8]
	line_size = [2, 2, 2]

	x_data = np.vstack((np.array(dataframe['Date']),)*3)

	
	y_data = np.array([
	    np.array(dataframe['negative']),
	    np.array(dataframe['positive']),
	    np.array(dataframe['uncertainty'])
	])

	


	fig = go.Figure()

	for i in range(0, 3):
		
		fig.add_trace(go.Scatter(x=x_data[i], y=y_data[i], mode='lines',
	        name=labels[i],
	        line=dict(color=colors[i], width=line_size[i]),
	        connectgaps=True,

	    ))
		fig.add_trace(go.Scatter(
	        x=[x_data[i][0], x_data[i][-1]],
	        y=[y_data[i][0], y_data[i][-1]],
	        mode='markers',
	        marker=dict(color=colors[i], size=mode_size[i])
	    ))
	#print(y_data,x_data)
	fig.update_layout(
	    xaxis=dict(
	        showline=True,
	        showgrid=False,
	        showticklabels=True,
	        linecolor='rgb(204, 204, 204)',
	        linewidth=2,
	        ticks='outside',
	        tickfont=dict(
	            family='Arial',
	            size=12,
	            color='rgb(82, 82, 82)',
	        ),
	    ),
	    yaxis=dict(
	        showgrid=False,
	        zeroline=False,
	        showline=False,
	        showticklabels=True,
	        side = "right"
	    ),
	    autosize=False,
	    margin=dict(
	        autoexpand=False,
	        l=100,
	        r=20,
	        t=110,
	    ),
	    showlegend=False,
	    plot_bgcolor='white'
	)

	annotations = []
	
	# Adding labels
	for y_trace, label, color in zip(y_data, labels, colors):
	    # labeling the left_side of the plot
	    annotations.append(dict(xref='paper', x=0.05, y=y_trace[0],
	                                  xanchor='right', yanchor='middle',
	                                  text=label,
	                                  font=dict(family='Arial',
	                                            size=16),
	                                  showarrow=False))
	    # labeling the right_side of the plot
	    annotations.append(dict(xref='paper', x=0.93, y=y_trace[len(y_trace)-1]+3,
	                                  xanchor='left', yanchor='middle',
	                                  text='{}%'.format(y_trace[len(y_trace)-1]),
	                                  font=dict(family='Arial',
	                                            size=16),
	                                  showarrow=False))

	# Title
	annotations.append(dict(xref='paper', yref='paper', x=0.0, y=1.05,
	                              xanchor='left', yanchor='bottom',
	                              text='Sentiment Analysis over TASLA News',
	                              font=dict(family='Arial',
	                                        size=30,
	                                        color='rgb(37,37,37)'),
	                              showarrow=False))
	# Source
	annotations.append(dict(xref='paper', yref='paper', x=0.5, y=-0.1,
	                              xanchor='center', yanchor='top',
	                              text='Source: https://newsapi.org/',
	                              font=dict(family='Arial',
	                                        size=12,
	                                        color='rgb(150,150,150)'),
	                              showarrow=False))

	fig.update_layout(annotations=annotations)
	print("save grpah...")
	fig.write_image("fig1.png")
	print("upload grpah...")
	upload_file("./fig1.png", "aldrichopenstorage", "fig1.png")
	#fig.show()
	print("Completed!!")
			


if __name__ == '__main__':

	dataframe=printPath(1, './StockOutput')
	DrawGraph(dataframe)
	
