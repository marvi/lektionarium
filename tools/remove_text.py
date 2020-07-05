import xml.etree.ElementTree as ET

tree = ET.parse('../api/src/main/resources/lectio/svk_lektionarium.xml')
root = tree.getroot()

for reading in root.findall("./day/readings/reading"):
    reading.text = ""

tree.write('svk_lektionarium_sans_text.xml', encoding="utf8")
